package com.admanager.colorcallscreen.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.service.CallManagerCompat;
import com.admanager.colorcallscreen.service.GsmCall;
import com.admanager.colorcallscreen.utils.FlashLightHelper;
import com.admanager.colorcallscreen.utils.Prefs;
import com.admanager.colorcallscreen.utils.Utils;
import com.admanager.colorcallscreen.view.FullScreenVideoView;
import com.admanager.core.AdmUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;

public class CCSInCallActivity extends AppCompatActivity {
    private static final String TAG = "InCallActivity";
    private static final String LOG_TAG = "InCallActivity";
    TextView textDuration;
    LinearLayout buttonHangup;
    LinearLayout buttonAnswer;
    TextView textStatus;
    TextView textDisplayName;
    TextView textNumber;
    ImageView bg_image;
    ImageView close;
    View imageContainer;
    View root;
    FullScreenVideoView bg_video;
    ImageView iv_portrait;
    LinearLayout container;
    GsmCall.Status lastStatus;
    String name = null;
    private Disposable updatesDisposable = Disposables.empty();
    private Disposable timerDisposable;
    private boolean showResult;

    public static void loadBgImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void loadBgImage(Context context, File file, ImageView imageView) {
        if (file == null || !file.exists()) {
            return;
        }
        Glide.with(context)
                .load(file)
                .into(imageView);
    }

    public static void updateView(GsmCall gsmCall, TextView textStatus, TextView textDuration, LinearLayout buttonHangup, LinearLayout buttonAnswer, TextView textDisplayName, TextView textNumber, ImageView iv_portrait) {
        Log.d(LOG_TAG, "updateView gsmCall: " + gsmCall);
        boolean active = gsmCall.getStatus().equals(GsmCall.Status.ACTIVE);
        boolean disconnected = gsmCall.getStatus().equals(GsmCall.Status.DISCONNECTED);
        boolean ringing = gsmCall.getStatus().equals(GsmCall.Status.RINGING);
        textDuration.setVisibility(!ringing ? View.VISIBLE : View.GONE);
        buttonHangup.setVisibility(!disconnected ? View.VISIBLE : View.GONE);
        buttonAnswer.setVisibility(ringing ? View.VISIBLE : View.GONE);

        textDisplayName.setText(gsmCall.getDisplayName());
        textNumber.setText(gsmCall.getNumber());

        String portraitUri = gsmCall.getPortraitUri();
        boolean existing = !TextUtils.isEmpty(portraitUri);
        Glide.with(iv_portrait.getContext())
                .load(existing ? Uri.parse(portraitUri) : R.drawable.unknown_user_bg)
                .into(iv_portrait);

        Context context = textStatus.getContext();
        switch (gsmCall.getStatus()) {
            case CONNECTING:
                textStatus.setText(context.getString(R.string.ccs_status_connecting));
                break;
            case DIALING:
                textStatus.setText(context.getString(R.string.ccs_status_dialing));
                break;
            case RINGING:
                textStatus.setText(context.getString(R.string.ccs_status_ringing));
                break;
            case DISCONNECTED:
                textStatus.setText(context.getString(R.string.ccs_status_disconnected));
                break;
            case HOLDING:
                textStatus.setText(context.getString(R.string.ccs_status_holding));
                break;
            case ACTIVE:
                textStatus.setText(context.getString(R.string.ccs_status_active));
                break;
            case UNKNOWN:
                textStatus.setText("");
                break;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.ccs_layout_ring_incoming);

        root = findViewById(R.id.root);
        container = findViewById(R.id.container);
        imageContainer = findViewById(R.id.imageContainer);
        bg_image = findViewById(R.id.bg_image);
        close = findViewById(R.id.close);
        bg_video = findViewById(R.id.bg_video);
        iv_portrait = findViewById(R.id.iv_portrait);
        textDuration = findViewById(R.id.text_duration);
        textStatus = findViewById(R.id.text_status);
        textDisplayName = findViewById(R.id.text_display_name);
        textNumber = findViewById(R.id.text_number);
        buttonHangup = findViewById(R.id.button_hangup);
        buttonAnswer = findViewById(R.id.button_answer);
        Utils.hideBottomNavigationBar(this);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallManagerCompat.getInstance(CCSInCallActivity.this).cancelCall();

            }
        });
        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallManagerCompat.getInstance(CCSInCallActivity.this).acceptCall();
            }
        });


    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        final CCSInCallActivity c = CCSInCallActivity.this;
        updatesDisposable = CallManagerCompat.getInstance(c).updates()
                .doOnEach(new Consumer<Notification<GsmCall>>() {
                    @Override
                    public void accept(Notification<GsmCall> gsmCallNotification) throws Exception {
                        GsmCall value = gsmCallNotification.getValue();
                        Log.i(LOG_TAG, "updated call: " + value);
                    }
                })
//                .doOnError(throwable -> Log.e(LOG_TAG, "Error processing call", throwable))
                .subscribe(new Consumer<GsmCall>() {
                    @Override
                    public void accept(GsmCall gsmCall) throws Exception {
                        Log.i(LOG_TAG, "subscribe gsmCall: " + gsmCall);
                        if (lastStatus == null && gsmCall.getStatus().equals(GsmCall.Status.DISCONNECTED)) {
                            finish();
                        }

                        ContactBean c1 = com.admanager.colorcallscreen.service.Utils.readContact(CCSInCallActivity.this, gsmCall.getNumber());
                        String name = Prefs.with(CCSInCallActivity.this).getSelectedBgForUser(c1);
                        if (name == null) {
                            name = Prefs.with(CCSInCallActivity.this).getSelectedBg();
                        }
                        if (name != null && !name.equals(CCSInCallActivity.this.name)) {
                            CCSInCallActivity.this.name = name;
                            File bgFile = Utils.getBgFile(CCSInCallActivity.this, name);
                            loadBgImage(CCSInCallActivity.this, bgFile, bg_image);
                        }

                        lastStatus = gsmCall.getStatus();
                        if (gsmCall.getStatus().equals(GsmCall.Status.RINGING)) {
                            showResults(false);
                            textDuration.setText(R.string.duration_zero);
                            FlashLightHelper.getInstance(CCSInCallActivity.this).start();
                        } else {
                            FlashLightHelper.getInstance(CCSInCallActivity.this).stop();
                        }
                        updateView(gsmCall, c.textStatus, c.textDuration, c.buttonHangup, c.buttonAnswer, c.textDisplayName, c.textNumber, c.iv_portrait);
                        updateTimer(gsmCall);
                    }
                });
    }

    public void updateTimer(final GsmCall gsmCall) {
        boolean active = gsmCall.getStatus().equals(GsmCall.Status.ACTIVE);
        boolean disconnected = gsmCall.getStatus().equals(GsmCall.Status.DISCONNECTED);
        if (active) {
            startTimer();
            showResults(false);
        } else if (disconnected) {
            stopTimer();

            showResults(true);
        }
    }

    private void showResults(boolean show) {
        bg_image.setVisibility(show ? View.GONE : View.VISIBLE);
        imageContainer.setVisibility(show ? View.GONE : View.VISIBLE);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) root.getLayoutParams();
        layoutParams.height = show ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.leftMargin = !show ? 0 : (int) AdmUtils.dpToPx(this, 12);
        layoutParams.rightMargin = !show ? 0 : (int) AdmUtils.dpToPx(this, 12);
        root.setLayoutParams(layoutParams);
        root.requestLayout();
        if (show) {
            root.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }

        container.setVisibility(show ? View.VISIBLE : View.GONE);
        close.setVisibility(show ? View.VISIBLE : View.GONE);
        showResult = show;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FlashLightHelper.getInstance(this).stop();
    }

    public void onPause() {
        super.onPause();
        updatesDisposable.dispose();
    }

    private void startTimer() {
        if (timerDisposable == null) {
            timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            textDuration.setText(Utils.toDurationString(aLong));
                        }
                    });
        }
    }

    private void stopTimer() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
        }
        timerDisposable = null;
    }


}