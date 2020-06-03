package com.admanager.colorcallscreen.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.admanager.colorcallscreen.ColorCallScreenApp;
import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.model.AfterCallCard;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.service.CallManagerCompat;
import com.admanager.colorcallscreen.service.GsmCall;
import com.admanager.colorcallscreen.utils.FlashLightHelper;
import com.admanager.colorcallscreen.utils.Prefs;
import com.admanager.colorcallscreen.utils.Utils;
import com.admanager.colorcallscreen.view.FullScreenVideoView;
import com.admanager.core.AdmUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;

public class CCSInCallActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InCallActivity";
    private static final String LOG_TAG = "InCallActivity";
    TextView textDuration;
    LinearLayout buttonHangup;
    LinearLayout buttonAnswer;
    LinearLayout additionalLayout;
    TextView textStatus;
    TextView textDisplayName;
    TextView textNumber;
    ImageView bg_image;
    ImageView close;
    View imageContainer;
    ConstraintLayout root;
    FullScreenVideoView bg_video;
    ImageView iv_portrait;
    GsmCall.Status lastStatus;
    String lastNumber;
    View layout_callend;
    ImageView image_call, image_write_message, image_addperson;
    EditText edit_quick_message;
    ImageView image_send_quick_message;
    String name = null;
    TextView[] textviews;
    FirebaseAnalytics firebaseAnalytics;
    private Disposable updatesDisposable = Disposables.empty();
    private Disposable timerDisposable;
    private LinearLayout buttons;

    public static void loadBgImage(Context context, String url, ImageView imageView, int cornerRadius) {
        RoundedCorners roundedCorners = new RoundedCorners(cornerRadius);
        CenterCrop centerCrop = new CenterCrop();
        MultiTransformation<Bitmap> transforms = new MultiTransformation<>(centerCrop, roundedCorners);
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(transforms))
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

    public static void updateView(GsmCall gsmCall, TextView textStatus, TextView textDuration,
                                  LinearLayout buttonHangup, LinearLayout buttonAnswer, LinearLayout buttons,
                                  TextView textDisplayName, TextView textNumber, ImageView iv_portrait) {
        Log.d(LOG_TAG, "updateView gsmCall: " + gsmCall);
        boolean active = gsmCall.getStatus().equals(GsmCall.Status.ACTIVE);
        boolean disconnected = gsmCall.getStatus().equals(GsmCall.Status.DISCONNECTED);
        boolean ringing = gsmCall.getStatus().equals(GsmCall.Status.RINGING);
        textDuration.setVisibility(!ringing ? View.VISIBLE : View.GONE);
        buttonHangup.setVisibility(!disconnected ? View.VISIBLE : View.GONE);
        buttons.setVisibility(!disconnected ? View.VISIBLE : View.GONE);
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flags, flags);
        getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.ccs_layout_ring_incoming);
        Utils.hideBottomNavigationBar(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        additionalLayout = findViewById(R.id.additional_layout);
        layout_callend = findViewById(R.id.layout_callend);
        root = findViewById(R.id.root);
        imageContainer = findViewById(R.id.imageContainer);
        bg_image = findViewById(R.id.bg_image);
        close = findViewById(R.id.close);
        bg_video = findViewById(R.id.bg_video);
        iv_portrait = findViewById(R.id.iv_portrait);
        textDuration = findViewById(R.id.text_duration);
        textStatus = findViewById(R.id.text_status);
        textDisplayName = findViewById(R.id.text_display_name);
        textNumber = findViewById(R.id.text_number);
        textviews = new TextView[]{textDuration, textStatus, textDisplayName, textNumber};
        buttonHangup = findViewById(R.id.button_hangup);
        buttonAnswer = findViewById(R.id.button_answer);
        buttons = findViewById(R.id.call_buttons);
        int l = (int) AdmUtils.dpToPx(this, 48);
        buttons.setPadding(l, 0, l, 0);

        edit_quick_message = findViewById(R.id.edit_quick_message);
        image_call = findViewById(R.id.image_call);
        image_write_message = findViewById(R.id.image_write_message);
        image_addperson = findViewById(R.id.image_addperson);
        image_send_quick_message = findViewById(R.id.image_send_quick_message);

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

        image_call.setOnClickListener(this);
        image_write_message.setOnClickListener(this);
        image_addperson.setOnClickListener(this);
        image_send_quick_message.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.e(TAG, "onClick" + v.getId());
        if (id == R.id.image_call) {
            if (lastNumber != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lastNumber));
                startActivityPreventNotFound(callIntent);
            }
        } else if (id == R.id.image_write_message) {
            if (lastNumber != null) {
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + lastNumber));
                startActivityPreventNotFound(sms_intent);
            }
        } else if (id == R.id.image_send_quick_message) {
            if (lastNumber != null) {
                Intent intent_write_sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + lastNumber));
                intent_write_sms.putExtra("sms_body", edit_quick_message.getText().toString());
                startActivityPreventNotFound(intent_write_sms);
            }
        } else if (id == R.id.image_addperson) {
            if (lastNumber != null) {
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, lastNumber);
                startActivityPreventNotFound(intent);
            }
        }
    }

    private void startActivityPreventNotFound(Intent intent) {
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.adm_ccs_intent_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        final CCSInCallActivity c = CCSInCallActivity.this;
        updatesDisposable = CallManagerCompat.getInstance(c).updates()
                .subscribe(new Consumer<GsmCall>() {
                    @Override
                    public void accept(GsmCall gsmCall) throws Exception {
                        Log.i(LOG_TAG, "subscribe gsmCall: " + gsmCall);
                        if (lastStatus == null && gsmCall.getStatus().equals(GsmCall.Status.DISCONNECTED)) {
                            getFlash().stop();
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
                        lastNumber = gsmCall.getNumber();
                        if (lastNumber != null && lastNumber.equals("number")) {
                            lastNumber = gsmCall.getDisplayName();
                        }
                        if (gsmCall.getStatus().equals(GsmCall.Status.RINGING)) {
                            showResults(false);
                            textDuration.setText(R.string.duration_zero);
                            textDuration.setVisibility(View.INVISIBLE);
                            getFlash().start();
                        } else {
                            textDuration.setVisibility(View.VISIBLE);
                            getFlash().stop();
                        }
                        updateView(gsmCall, c.textStatus, c.textDuration, c.buttonHangup, c.buttonAnswer, c.buttons, c.textDisplayName, c.textNumber, c.iv_portrait);
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
        bg_image.setAlpha(show ? 0.2f : 1f);
        imageContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        layout_callend.setVisibility(!show ? View.GONE : View.VISIBLE);

        for (TextView textview : textviews) {
            int radius = show ? 0 : 8;
            int color = show ? android.R.color.black : android.R.color.white;
            textview.setShadowLayer(radius, -1, 1, ContextCompat.getColor(this, R.color.colorPrimary));
            textview.setTextColor(ContextCompat.getColor(this, color));
        }
        if (show) {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            root.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }

        close.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            getFlash().stop();

            ColorCallScreenApp instance = ColorCallScreenApp.getInstance();
            if (instance != null) {
                if (instance.afterCallLayout != null) {
                    additionalLayout.removeAllViews();
                    instance.afterCallLayout.inflateInto(this, additionalLayout, lastNumber);
                }

                if (instance.afterCallCards != null) {
                    additionalLayout.removeAllViews();
                    for (AfterCallCard params : instance.afterCallCards) {
                        View v = createAfterCallLayout(this, params);
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int margin = 10;
                        llp.setMargins(0, margin, 0, margin);
                        v.setLayoutParams(llp);
                        additionalLayout.addView(v);
                    }
                }
            }
        }
    }

    public View createAfterCallLayout(final Context context, final AfterCallCard parameter) {

        // IMPORTANT NOTE! : This code is only an example, you MUST change below code
        View view = LayoutInflater.from(context).inflate(R.layout.adm_ccs_default_layout_after_call, null);
        TextView title = view.findViewById(R.id.title);
        TextView subtitle = view.findViewById(R.id.subtitle);
        ImageView image = view.findViewById(R.id.image);

        title.setVisibility(parameter.getTitle() == 0 ? View.GONE : View.VISIBLE);
        if (parameter.getTitle() != 0) {
            title.setText(getString(parameter.getTitle()));
        }

        subtitle.setVisibility(parameter.getSubtitle() == 0 ? View.GONE : View.VISIBLE);
        if (parameter.getSubtitle() != 0) {
            subtitle.setText(getString(parameter.getSubtitle()));
        }

        image.setVisibility(parameter.getImage() == 0 ? View.GONE : View.VISIBLE);
        if (parameter.getImage() != 0) {
            Glide.with(context)
                    .load(parameter.getImage())
                    .into(image);
        }

        if (parameter.getIntent() != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        context.startActivity(parameter.getIntent());
                    } catch (Exception e) {
                    }
                    String event = "aftercall_card_click_";

                    logCardClickEvent(event, parameter);

                }

            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForeground(null);
        }

        return view;
    }

    private void logCardClickEvent(String event, AfterCallCard parameter) {
        try {
            String resourceEntryName = getResources().getResourceEntryName(parameter.getTitle());
            event += resourceEntryName;
            if (event.length() > 39) {
                event = event.substring(0, 39);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        firebaseAnalytics.logEvent(event, null);
    }

    private FlashLightHelper getFlash() {
        return FlashLightHelper.getInstance(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getFlash().stop();
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