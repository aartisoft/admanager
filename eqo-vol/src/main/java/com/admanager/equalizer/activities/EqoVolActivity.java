package com.admanager.equalizer.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.admanager.core.AdmUtils;
import com.admanager.core.Consts;
import com.admanager.equalizer.EqoVolApp;
import com.admanager.equalizer.R;
import com.admanager.equalizer.models.EqualizerPreset;
import com.admanager.equalizer.tasks.LedView;
import com.admanager.equalizer.tasks.NeedleRoundView;
import com.admanager.equalizer.tasks.RingRoundView;
import com.admanager.equalizer.utilities.CustomSpinnerAdapter;
import com.admanager.equalizer.utilities.DynamicWidthSpinner;
import com.admanager.equalizer.utilities.NotificationUtils;
import com.admanager.equalizer.utilities.ProgressBarAnimation;
import com.admanager.equalizer.utilities.SharedPrefUtils;
import com.admanager.equalizer.utilities.VerticalSeekBar;
import com.admanager.musicplayer.activities.MPMainActivity;
import com.admanager.musicplayer.listeners.MPIPlayer;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.services.MediaPlayerService;
import com.admanager.musicplayer.utilities.ExternalStorageUtil;

import java.util.ArrayList;


public class EqoVolActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, MPIPlayer {
    public static final int REQUEST_CODE_MUSIC_PLAYER = 101;
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 11;
    //Volume
    private static final int LEVEL_MUTE = 0;
    private static final int LEVEL_100 = 100;
    private static final int LEVEL_120 = 120;
    private static final int LEVEL_140 = 140;
    private static final int LEVEL_160 = 160;
    private static final int LEVEL_180 = 180;
    private static final int LEVEL_MAX = 200;
    private String notifStarterName;
    private LinearLayout maSelection2Layout;
    private ImageView bgOfLayout;
    private ProgressBarAnimation animation;
    private MENU_COMPLETE menu_complete = MENU_COMPLETE.NONE;
    private FrameLayout maVolumeTopLayout, maVolumeBottomLayout, maEqualizerTopLayout, maEqualizerBottomLayout;
    private int LEVEL_20 = 20;
    private int LEVEL_40 = 40;
    private int LEVEL_60 = 60;
    private int LEVEL_80 = 80;
    private AppCompatSeekBar maVolumeSeekBar;
    private TextView maVolumeMuteButton, maVolume20Button, maVolume40Button, maVolume60Button, maVolume80Button, maVolume100Button;
    private TextView maVolume120Button, maVolume140Button, maVolume160Button, maVolume180Button, maVolumeMaxButton;
    private NeedleRoundView maVolumeNeedleRoundView;
    private RingRoundView maVolumeRingRoundView;
    private int volumeMaxLevel;
    private int volumeLevel;
    private LoudnessEnhancer loudnessEnhancer;
    //Equalizer
    private NeedleRoundView bassNeedleRoundView;
    private RingRoundView bassRingRoundView;
    private NeedleRoundView visualNeedleRoundView;
    private RingRoundView visualRingRoundView;
    private VerticalSeekBar[] seekBars;
    private DynamicWidthSpinner presetSpinner;
    private CheckBox equalizerCheckBox;
    private CustomSpinnerAdapter equalizerPresetSpinnerAdapter;
    private short lowerEqualizerBandLevel;
    //Main
    private LedView leftLedView;
    private LedView rightLedView;
    private ImageView maPlayImage;
    private TextView maMusicTrack, maMusicArtist;
    private View maMusicInfoLayout, maOpenMusicLayout;
    private AudioManager mAudioManager;
    private Toolbar toolbar;
    private ImageView maVolumeSelection;
    private ImageView maEqualizerSelection;
    private Handler m_Handler;
    private boolean secondClick = false;
    private Class startClass;
    private MediaPlayerService mediaPlayerService;
    private final Runnable m_Runnable = () -> {
        setLevel();
        startHandler();
    };
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mediaPlayerService = ((MediaPlayerService.LocalBinder) service).getService();

            controlForService();
            //extraVolumeService.addPlayerListener(MainActivity.this);

            /*new Handler().postDelayed(() -> {

            }, 3000);*/

            // Tell the user about this for our demo.
            //Toast.makeText(Binding.this, R.string.local_service_connected, Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mediaPlayerService = null;
            //Toast.makeText(Binding.this, R.string.local_service_disconnected,Toast.LENGTH_SHORT).show();
        }
    };
    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private boolean mShouldUnbind;

    public static void start(Context context) {
        Intent intent = new Intent(context, EqoVolActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adm_activity_equalizer);
        initViews();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.mp_ic_back));

        }


        m_Handler = new Handler();

        //Enjoy native and admob
        //RemoteConfigHelper.init(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            volumeMaxLevel = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        setTitle(R.string.volume_booster);
        boolean isStartNotification = SharedPrefUtils.getStartNotification(this);

        if (isStartNotification) {
            NotificationUtils.createNotification(this);
        }

        maVolumeSelection.setOnClickListener(v -> setVolumeSelection());
        maEqualizerSelection.setOnClickListener(v -> setEqualizerSelection());

        //Music
        maMusicInfoLayout.setOnClickListener(v -> openMusicPlayer());

        maOpenMusicLayout.setOnClickListener(v -> openMusicPlayer());

        maMusicTrack.setHorizontallyScrolling(true);
        maMusicTrack.setSingleLine(true);
        maMusicTrack.setSelected(true);
        maMusicTrack.setFocusable(true);
        maMusicTrack.setFocusableInTouchMode(true);
        maMusicTrack.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        maMusicTrack.setText(getResources().getString(R.string.open_music_player));

        maMusicArtist.setHorizontallyScrolling(true);
        maMusicArtist.setSingleLine(true);
        maMusicArtist.setSelected(true);
        maMusicArtist.setFocusable(true);
        maMusicArtist.setFocusableInTouchMode(true);
        maMusicArtist.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        TextView maOpenMusicTextView = findViewById(R.id.maOpenMusicTextView);
        maOpenMusicTextView.setHorizontallyScrolling(true);
        maOpenMusicTextView.setSingleLine(true);
        maOpenMusicTextView.setSelected(true);
        maOpenMusicTextView.setFocusable(true);
        maOpenMusicTextView.setFocusableInTouchMode(true);
        maOpenMusicTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);


        findViewById(R.id.maImagePlayLayout).setOnClickListener(v -> playOrPauseAction());
        findViewById(R.id.maImagePreviousLayout).setOnClickListener(v -> playPreviousAction());
        findViewById(R.id.maImageNextLayout).setOnClickListener(v -> playNextAction());

        initMusic();

        SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler());
        this.getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);


        //Volume
        int level20progress = (int) ((volumeMaxLevel / 100.0f) * 20.0f);
        LEVEL_20 = (int) ((level20progress / (volumeMaxLevel * 1.0f)) * 100.0f);

        int level40progress = (int) ((volumeMaxLevel / 100.0f) * 40.0f);
        LEVEL_40 = (int) ((level40progress / (volumeMaxLevel * 1.0f)) * 100.0f);

        int level60progress = (int) ((volumeMaxLevel / 100.0f) * 60.0f);
        LEVEL_60 = (int) ((level60progress / (volumeMaxLevel * 1.0f)) * 100.0f);

        int level80progress = (int) ((volumeMaxLevel / 100.0f) * 80.0f);
        LEVEL_80 = (int) ((level80progress / (volumeMaxLevel * 1.0f)) * 100.0f);


        maVolumeMuteButton.setOnClickListener(v -> setVolume(LEVEL_MUTE, true));

        maVolume20Button.setOnClickListener(v -> setVolume(LEVEL_20, true));

        maVolume40Button.setOnClickListener(v -> setVolume(LEVEL_40, true));

        maVolume60Button.setOnClickListener(v -> setVolume(LEVEL_60, true));

        maVolume80Button.setOnClickListener(v -> setVolume(LEVEL_80, true));

        maVolume100Button.setOnClickListener(v -> setVolume(LEVEL_100, true));

        maVolume120Button.setOnClickListener(v -> setVolume(LEVEL_120, true));

        maVolume140Button.setOnClickListener(v -> setVolume(LEVEL_140, true));

        maVolume160Button.setOnClickListener(v -> setVolume(LEVEL_160, true));

        maVolume180Button.setOnClickListener(v -> setVolume(LEVEL_180, true));

        maVolumeMaxButton.setOnClickListener(v -> setVolume(LEVEL_MAX, true));

        if (mAudioManager != null) {
            maVolumeSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int initProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maVolumeSeekBar.setProgress(initProgress);

            this.volumeLevel = (int) ((initProgress * 1.0f / volumeMaxLevel) * 100.0f);
            controlVolumeButtons(volumeLevel);
        }

        maVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                if (mAudioManager != null) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);  // 0 can also be changed to AudioManager.FLAG_PLAY_SOUND

                    volumeLevel = (int) ((progress * 1.0f / volumeMaxLevel) * 100.0f);

                    controlVolumeButtons((int) ((progress * 1.0f / volumeMaxLevel) * 100.0f));
                }
            }
        });

        maVolumeNeedleRoundView.getParent().requestDisallowInterceptTouchEvent(true);
        maVolumeNeedleRoundView.setDegree(45.0f);
        maVolumeNeedleRoundView.setNeedleChangedListener(this::adjustBoostVolume);

        maVolumeRingRoundView.setDegree(45.0f);

        //Equalizer
        equalizerCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> changeEqualizer(isChecked));
        setEqualizerEnabled(equalizerCheckBox.isChecked());

        //Bass
        bassNeedleRoundView.getParent().requestDisallowInterceptTouchEvent(true);
        bassNeedleRoundView.setDegree(45.0f);
        bassNeedleRoundView.setNeedleChangedListener(this::adjustBass);

        bassRingRoundView.setDegree(45.0f);

        //Visual
        visualNeedleRoundView.getParent().requestDisallowInterceptTouchEvent(true);
        visualNeedleRoundView.setDegree(45.0f);
        visualNeedleRoundView.setNeedleChangedListener(this::adjustVisual);

        visualRingRoundView.setDegree(45.0f);

        //Vertical Seek Bars
        seekBars = new VerticalSeekBar[5];
        seekBars[0] = findViewById(R.id.maSeekbar_1);
        seekBars[1] = findViewById(R.id.maSeekbar_2);
        seekBars[2] = findViewById(R.id.maSeekbar_3);
        seekBars[3] = findViewById(R.id.maSeekbar_4);
        seekBars[4] = findViewById(R.id.maSeekbar_5);

        //Vertical Seek Bars Title
        TextView[] seekbar_Titles = new TextView[5];
        seekbar_Titles[0] = findViewById(R.id.maSeekbar_1_Title);
        seekbar_Titles[1] = findViewById(R.id.maSeekbar_2_Title);
        seekbar_Titles[2] = findViewById(R.id.maSeekbar_3_Title);
        seekbar_Titles[3] = findViewById(R.id.maSeekbar_4_Title);
        seekbar_Titles[4] = findViewById(R.id.maSeekbar_5_Title);

        presetSpinner.setOnItemSelectedEvenIfUnchangedListener(this);

        ArrayList<EqualizerPreset> equalizerPresetArrayList = new ArrayList<>();

        Equalizer equalizerTemp = null;
        try {
            equalizerTemp = new Equalizer(Integer.MAX_VALUE, 0);

            lowerEqualizerBandLevel = equalizerTemp.getBandLevelRange()[0];
            short upperEqualizerBandLevel = equalizerTemp.getBandLevelRange()[1];

            for (short i = 0; i < 5; i++) {
                final short equalizerBandIndex = i;
                seekBars[i].setId(i);
                seekBars[i].setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                seekBars[i].setProgress(equalizerTemp.getBandLevel(i));

                seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mediaPlayerService != null) {
                            mediaPlayerService.setEqualizerBandLevel(equalizerBandIndex, (short) (progress + lowerEqualizerBandLevel));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                String frequencyTitle;
                int frequency = equalizerTemp.getCenterFreq(i);
                if ((frequency > 1000000)) {
                    if ((frequency % 1000000) == 0) {
                        frequencyTitle = equalizerTemp.getCenterFreq(i) / 1000000 + " kHz";
                    } else {
                        frequencyTitle = ((equalizerTemp.getCenterFreq(i) / 1000000.0f) * 10.0f) / 10.0f + " kHz";
                    }
                } else {
                    frequencyTitle = equalizerTemp.getCenterFreq(i) / 1000 + " Hz";
                }
                seekbar_Titles[i].setText(frequencyTitle);
            }

            for (short i = 0; i < equalizerTemp.getNumberOfPresets(); i++) {
                EqualizerPreset equalizerPreset = new EqualizerPreset();
                equalizerPreset.setPresetNumber(i);
                equalizerPreset.setPresetName(equalizerTemp.getPresetName(i));
                equalizerPreset.setPosition(i);

                equalizerPresetArrayList.add(equalizerPreset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (equalizerTemp != null) {
                equalizerTemp.release();
            }
        }

        if (!AdmUtils.isContextInvalid(this)) {
            equalizerPresetSpinnerAdapter = new CustomSpinnerAdapter(this, equalizerPresetArrayList);

            //equalizerPresetSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

            //Set the text color of the Spinner's selected view (not a drop down list view)
            //presetSpinner.setSelection(0);
        }


        equalizerOff(false);

        adjustVisualizer();

        if (com.admanager.musicplayer.utilities.SharedPrefUtils.getFirstActionView(this)) {
            Uri actionUri = getIntent().getParcelableExtra(com.admanager.musicplayer.utilities.Constants.ACTION_URI_NAME);

            Intent intent2 = new Intent(this, MPMainActivity.class);
            intent2.putExtra(com.admanager.musicplayer.utilities.Constants.ACTION_URI_NAME, actionUri);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivityForResult(intent2, REQUEST_CODE_MUSIC_PLAYER);
        }

        //Service must be called after temp equalizer
        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        doBindService();

        if (getIntent() != null) {
            notifStarterName = getIntent().getStringExtra("starterClass");
        }
        initSettings();
    }

    private void initSettings() {
        EqoVolApp instance = EqoVolApp.getInstance();
        if (instance == null) {
            Log.e(Consts.TAG, "init Maps module in Application class");
            finish();
        } else {
            if (instance.ads != null) {
                instance.ads.loadTop(this, findViewById(R.id.topBanner));
                instance.ads.loadBottom(this, findViewById(R.id.bottomBanner));
            }
            if (instance.canShowEqualizer && instance.canShowVolumeBooster) {
                maSelection2Layout.setVisibility(View.VISIBLE);
            } else {
                maSelection2Layout.setVisibility(View.GONE);
                bgOfLayout.setVisibility(View.GONE);
                if (instance.canShowEqualizer) {
                    changeEqualizer(true);
                    setEqualizerSelection();
                    equalizerCheckBox.setVisibility(View.VISIBLE);
                } else {
                    setVolumeSelection();
                    changeEqualizer(false);
                    equalizerCheckBox.setVisibility(View.GONE);
                }
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void initViews() {
        maVolumeSelection = findViewById(R.id.maVolumeSelection);
        maSelection2Layout = findViewById(R.id.maSelection2Layout);
        bgOfLayout = findViewById(R.id.bgOfLayout);
        maEqualizerSelection = findViewById(R.id.maEqualizerSelection);
        toolbar = findViewById(R.id.toolbar);
        maVolumeTopLayout = findViewById(R.id.maVolumeTopLayout);
        maVolumeBottomLayout = findViewById(R.id.maVolumeBottomLayout);
        maEqualizerTopLayout = findViewById(R.id.maEqualizerTopLayout);
        maEqualizerBottomLayout = findViewById(R.id.maEqualizerBottomLayout);
        maMusicInfoLayout = findViewById(R.id.maMusicInfoLayout);
        maOpenMusicLayout = findViewById(R.id.maOpenMusicLayout);
        maMusicTrack = findViewById(R.id.maMusicTrack);
        maMusicArtist = findViewById(R.id.maMusicArtist);
        maPlayImage = findViewById(R.id.maPlayImage);
        maVolumeMuteButton = findViewById(R.id.maVolumeMuteButton);
        maVolume20Button = findViewById(R.id.maVolume20Button);
        maVolume40Button = findViewById(R.id.maVolume40Button);
        maVolume60Button = findViewById(R.id.maVolume60Button);
        maVolume80Button = findViewById(R.id.maVolume80Button);
        maVolume100Button = findViewById(R.id.maVolume100Button);
        maVolume120Button = findViewById(R.id.maVolume120Button);
        maVolume140Button = findViewById(R.id.maVolume140Button);
        maVolume160Button = findViewById(R.id.maVolume160Button);
        maVolume180Button = findViewById(R.id.maVolume180Button);
        maVolumeMaxButton = findViewById(R.id.maVolumeMaxButton);
        maVolumeSeekBar = findViewById(R.id.maVolumeSeekbar);
        maVolumeNeedleRoundView = findViewById(R.id.maVolumeNeedleRoundView);
        maVolumeRingRoundView = findViewById(R.id.maVolumeRingRoundView);
        equalizerCheckBox = findViewById(R.id.maEqualizerCheckBox);
        bassNeedleRoundView = findViewById(R.id.maBassNeedleRoundView);
        bassRingRoundView = findViewById(R.id.maBassRingRoundView);
        visualNeedleRoundView = findViewById(R.id.maVisualNeedleRoundView);
        visualRingRoundView = findViewById(R.id.maVisualRingRoundView);
        presetSpinner = findViewById(R.id.maPresetSpinner);
        //LedView
        leftLedView = findViewById(R.id.maLeftLedView);
        rightLedView = findViewById(R.id.maRightLedView);
    }

    private void controlVolumeChange() {
        if (mAudioManager != null) {
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int currentVolumeLevel = (int) ((currentVolume * 1.0f / maxVolume) * 100.0f);

            if (currentVolumeLevel == volumeLevel) return;

            if (volumeLevel >= 100 && currentVolumeLevel == 100) return;

            maVolumeSeekBar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int initProgress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maVolumeSeekBar.setProgress(initProgress);

            controlVolumeButtons((int) ((initProgress * 1.0f / volumeMaxLevel) * 100.0f));

            maVolumeNeedleRoundView.setDegree(NeedleRoundView.MIN_DEGREE);
            maVolumeRingRoundView.setDegree(NeedleRoundView.MIN_DEGREE);

            amplifyVolume(currentVolumeLevel);
        }
    }

    private void initMusic() {
        if (mAudioManager == null) mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if (mAudioManager != null && mAudioManager.isMusicActive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                dispatchKey(KeyEvent.KEYCODE_MEDIA_PAUSE);

                dispatchKey(KeyEvent.KEYCODE_MEDIA_PLAY);

                new Handler().postDelayed(() ->
                {
                    if (!mAudioManager.isMusicActive()) {
                        dispatchKey(KeyEvent.KEYCODE_MEDIA_PLAY);
                    }
                }, 100);
            }
        }
    }

    private void openMusicPlayer() {
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        controlPlayButtonWithAudio();

        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopHandler();
    }

    private void setLevel() {
        if (mediaPlayerService == null) return;

        int level = mediaPlayerService.getLevelFromVisualizer();

        leftLedView.setLevelNumber(level);
        rightLedView.setLevelNumber(level);
    }

    private void startHandler() {
        synchronized (this) {
            if (m_Handler != null) {
                m_Handler.removeCallbacks(m_Runnable);
                m_Handler.postDelayed(m_Runnable, 100);
            }
        }
    }

    private void stopHandler() {
        if (m_Handler != null) {
            m_Handler.removeCallbacks(m_Runnable);
        }
    }

    private void adjustVisualizer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
        } else {
            adjustVisualizerYes();
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == com.admanager.musicplayer.utilities.Constants.REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (com.admanager.musicplayer.utilities.ContextUtils.isContextValid(this)) {
                    Toast.makeText(this, getResources().getString(com.admanager.musicplayer.R.string.mp_read_granted), Toast.LENGTH_LONG).show();

                    gotoMusicPlayerActivity();
                }
            } else {
                if (com.admanager.musicplayer.utilities.ContextUtils.isContextValid(this)) {
                    Toast.makeText(this, getResources().getString(com.admanager.musicplayer.R.string.mp_not_granted), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == REQUEST_PERMISSION_RECORD_AUDIO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adjustVisualizerYes();
            } else {
                if (!AdmUtils.isContextInvalid(this)) {
                    Toast toast = Toast.makeText(this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    private void adjustVisualizerYes() {
        if (mediaPlayerService != null) {
            mediaPlayerService.createVisualizer();
        }
    }

    private void controlPlayButtonWithAudio() {
        if (mAudioManager == null) mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        if (mAudioManager != null && mAudioManager.isMusicActive()) {
            maPlayImage.setImageResource(R.drawable.ic_pause);
        } else {
            maPlayImage.setImageResource(R.drawable.ic_play);
        }
    }

    private void controlPlayButtonWithSharedPref() {
        boolean playerState = SharedPrefUtils.getPlayerState(this);

        if (playerState) {
            maPlayImage.setImageResource(R.drawable.ic_pause);
        } else {
            maPlayImage.setImageResource(R.drawable.ic_play);
        }
    }

    private void playOrPauseAction() {
        if (mediaPlayerService == null) return;

        if (mediaPlayerService.isPlaying()) {
            mediaPlayerService.pause();

            maPlayImage.setImageResource(R.drawable.ic_play);
        } else {
            Track track = mediaPlayerService.getTrack();
            if (track != null) {
                mediaPlayerService.play();

                maPlayImage.setImageResource(R.drawable.ic_pause);

                setTrackInformation(track);
            }
        }
    }

    private void dispatchKey(int keyCode) {
        if (mAudioManager == null) mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        long uptimeMillis = SystemClock.uptimeMillis();

        KeyEvent event_1 = new KeyEvent(uptimeMillis, uptimeMillis, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent event_2 = new KeyEvent(uptimeMillis, uptimeMillis, KeyEvent.ACTION_UP, keyCode, 0);

        mAudioManager.dispatchMediaKeyEvent(event_1);
        mAudioManager.dispatchMediaKeyEvent(event_2);

        new Handler().postDelayed(this::controlPlayButtonWithAudio, 1000);
    }

    private void playPreviousAction() {
        if (mediaPlayerService == null) return;

        mediaPlayerService.playPrevious();

        controlTrackAction();
    }

    private void playNextAction() {
        if (mediaPlayerService == null) return;

        mediaPlayerService.playNext();

        controlTrackAction();
    }

    private void controlTrackAction() {
        if (mediaPlayerService != null) {
            Track track = mediaPlayerService.getTrack();

            if (track != null) {
                setTrackInformation(track);
            }

            if (mediaPlayerService.isPlaying()) {
                maPlayImage.setImageResource(R.drawable.ic_pause);
            } else {
                maPlayImage.setImageResource(R.drawable.ic_play);
            }
        }
    }

    private void setTrackInformation(Track track) {
        if (track == null) return;

        if (TextUtils.isEmpty(track.getTitle()) && TextUtils.isEmpty(track.getArtist())) {
            maOpenMusicLayout.setVisibility(View.VISIBLE);
            maMusicInfoLayout.setVisibility(View.GONE);
        } else {
            maMusicInfoLayout.setVisibility(View.VISIBLE);
            maOpenMusicLayout.setVisibility(View.GONE);

            maMusicTrack.setText(track.getTitle());
            maMusicArtist.setText(track.getArtist());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();

        if (mediaPlayerService != null) {
            mediaPlayerService = null;
        }

        equalizerOff(false);
    }

    private void gotoMusicPlayerActivity() {
        Intent intent = new Intent(this, MPMainActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MUSIC_PLAYER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_MUSIC_PLAYER && resultCode == RESULT_OK) {
            if (mediaPlayerService != null && mediaPlayerService.getTrack() != null) {
                setTrackInformation(mediaPlayerService.getTrack());
            }
        }
    }

    private void doBindService() {
        // Attempts to establish a connection with the service.  We use an
        // explicit class name because we want a specific service
        // implementation that we know will be running in our own process
        // (and thus won't be supporting component replacement by other
        // applications).
        if (bindService(new Intent(this, MediaPlayerService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;

        } else {
            //Log.e("MY_APP_TAG", "Error: The requested service doesn't " +"exist, or this client isn't allowed access to it.");
        }
    }

    private void doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    private void controlVolumeButtons(int volumeLevel) {
        if (volumeLevel == LEVEL_MUTE)
            maVolumeMuteButton.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_20)
            maVolume20Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_40)
            maVolume40Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_60)
            maVolume60Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_80)
            maVolume80Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_100)
            maVolume100Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_120)
            maVolume120Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_140)
            maVolume140Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_160)
            maVolume160Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_180)
            maVolume180Button.setBackgroundResource(R.drawable.btn_active);
        else if (volumeLevel == LEVEL_MAX)
            maVolumeMaxButton.setBackgroundResource(R.drawable.btn_active);

        if (volumeLevel != LEVEL_MUTE)
            maVolumeMuteButton.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_20) maVolume20Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_40) maVolume40Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_60) maVolume60Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_80) maVolume80Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_100)
            maVolume100Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_120)
            maVolume120Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_140)
            maVolume140Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_160)
            maVolume160Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_180)
            maVolume180Button.setBackgroundResource(R.drawable.btn_passive);
        if (volumeLevel != LEVEL_MAX)
            maVolumeMaxButton.setBackgroundResource(R.drawable.btn_passive);
    }

    private void setVolume(int volumeLevel, boolean buttonClick) {
        this.volumeLevel = volumeLevel;

        int progress = volumeMaxLevel * volumeLevel / 100;

        if (volumeLevel <= 100 && mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);  // 0 can also be changed to AudioManager.FLAG_PLAY_SOUND

            maVolumeSeekBar.setProgress(progress);

            maVolumeNeedleRoundView.setDegree(NeedleRoundView.MIN_DEGREE);
            maVolumeRingRoundView.setDegree(NeedleRoundView.MIN_DEGREE);
        } else {
            if (progress >= volumeMaxLevel && mAudioManager != null) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMaxLevel, 0);  // 0 can also be changed to AudioManager.FLAG_PLAY_SOUND

                maVolumeSeekBar.setProgress(volumeMaxLevel);

                float degree = ((volumeLevel - 100) / 100.0f) * (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f + NeedleRoundView.MIN_DEGREE;
                if (buttonClick) maVolumeNeedleRoundView.setDegree(degree);
                maVolumeRingRoundView.setDegree(degree);
            }
        }

        amplifyVolume(volumeLevel);

        controlVolumeButtons(volumeLevel);
    }

    private void amplifyVolume(int volumeLevel) {
        if (mediaPlayerService == null) return;

        int playerSessionId = mediaPlayerService.getAudioSession();

        try {
            if (volumeLevel <= 100) {
                if (loudnessEnhancer != null) {
                    loudnessEnhancer.setTargetGain(0);
                    /*loudnessEnhancer.setEnabled(false);
                    loudnessEnhancer.release();
                    loudnessEnhancer = null;*/
                }
            } else {
                if (loudnessEnhancer == null) {
                    loudnessEnhancer = new LoudnessEnhancer(playerSessionId);
                    loudnessEnhancer.setEnabled(true);
                }

                if (volumeLevel % 10 == 0) {
                    loudnessEnhancer.setTargetGain((volumeLevel - 100) * 40); // Max = 8000 / 2 = 4000
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustBoostVolume(float degree) {
        if (degree >= NeedleRoundView.MIN_DEGREE && degree <= NeedleRoundView.MAX_DEGREE) {
            int extraVolumeLevel = (int) (((degree - NeedleRoundView.MIN_DEGREE) * 1.0f / (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f) * 100.0f);

            this.volumeLevel = extraVolumeLevel + 100;

            setVolume(volumeLevel, false);
        }
    }

    private void setEqualizerEnabled(boolean enabled) {
        if (mediaPlayerService != null) {
            mediaPlayerService.setEqualizerEnabled(enabled);
        }
    }

    private void changeEqualizer(boolean isChecked) {
        setEqualizerEnabled(isChecked);

        if (isChecked) {
            equalizerOn();
        } else {
            equalizerOff(true);
        }
    }

    private void equalizerOn() {
        if (equalizerPresetSpinnerAdapter != null) {
            equalizerPresetSpinnerAdapter.setShowFirstItemEmpty(false);
        }

        if (presetSpinner != null) {
            presetSpinner.setEnabled(true);
            presetSpinner.setSelection(0);
        }

        for (int i = 0; i < 5; i++) {
            if (seekBars[i] != null) {
                seekBars[i].setEnabled(true);
            }
        }

        if (bassNeedleRoundView != null) {
            bassNeedleRoundView.setEnabled(true);
            bassNeedleRoundView.setDegree(45.0f);
            bassRingRoundView.setEnabled(true);
            bassRingRoundView.setDegree(45.0f);
        }

        if (visualNeedleRoundView != null) {
            visualNeedleRoundView.setEnabled(true);
            visualNeedleRoundView.setDegree(45.0f);
            visualRingRoundView.setEnabled(true);
            visualRingRoundView.setDegree(45.0f);
        }
    }

    private void equalizerOff(boolean spinnerSelectionEnabled) {
        if (equalizerPresetSpinnerAdapter != null) {
            equalizerPresetSpinnerAdapter.setShowFirstItemEmpty(true);
        }

        if (presetSpinner != null) {

            if (spinnerSelectionEnabled) {
                presetSpinner.setSelection(0);
            }

            presetSpinner.setEnabled(false);
        }

        for (int i = 0; i < 5; i++) {
            if (seekBars[i] != null) {
                seekBars[i].setProgress(0);
                seekBars[i].setEnabled(false);
            }
        }

        if (bassNeedleRoundView != null) {
            bassNeedleRoundView.setEnabled(false);
            bassNeedleRoundView.setDegree(45.0f);
            bassRingRoundView.setEnabled(false);
            bassRingRoundView.setDegree(45.0f);
        }

        if (visualNeedleRoundView != null) {
            visualNeedleRoundView.setEnabled(false);
            visualNeedleRoundView.setDegree(45.0f);
            visualRingRoundView.setEnabled(false);
            visualRingRoundView.setDegree(45.0f);
        }
    }

    private void adjustBass(float degree) {
        if (bassRingRoundView != null) {
            bassRingRoundView.setDegree(degree);
        }

        if (mediaPlayerService != null) {
            mediaPlayerService.setBassBoostStrength((short) ((degree - NeedleRoundView.MIN_DEGREE) / (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1000));
        }
    }

    private void adjustVisual(float degree) {
        if (visualRingRoundView != null) {
            visualRingRoundView.setDegree(degree);
        }

        if (mediaPlayerService != null) {
            mediaPlayerService.setVirtualizerStrength((short) ((degree - NeedleRoundView.MIN_DEGREE) / (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1000));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (equalizerPresetSpinnerAdapter != null) {
            EqualizerPreset equalizerPreset = equalizerPresetSpinnerAdapter.getItem(position);

            if (mediaPlayerService != null && mediaPlayerService.equalizer != null && equalizerPreset != null) {
                try {
                    mediaPlayerService.equalizer.usePreset(equalizerPreset.getPresetNumber());
                } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) {
                    e.printStackTrace();
                }

                for (short i = 0; i < 5; i++) {
                    if (seekBars[i] != null) {
                        animation = new ProgressBarAnimation(seekBars[i], seekBars[i].getProgress(), mediaPlayerService.equalizer.getBandLevel(i) - lowerEqualizerBandLevel);
                        seekBars[i].setProgress(mediaPlayerService.equalizer.getBandLevel(i) - lowerEqualizerBandLevel);
                        animation.setDuration(2000);
                        seekBars[i].startAnimation(animation);
                    }
                }

                if (equalizerPresetSpinnerAdapter != null) {
                    equalizerPresetSpinnerAdapter.setSelectedPosition(position);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void controlForService() {
        adjustVisualizer();

        if (equalizerCheckBox != null) {
            setEqualizerEnabled(equalizerCheckBox.isChecked());
        }
    }

    private void setVolumeSelection() {
        setTitle(getResources().getString(R.string.volume_booster));

        maVolumeTopLayout.setVisibility(View.VISIBLE);
        maVolumeBottomLayout.setVisibility(View.VISIBLE);

        maEqualizerTopLayout.setVisibility(View.GONE);
        maEqualizerBottomLayout.setVisibility(View.GONE);

        equalizerCheckBox.setVisibility(View.GONE);

        maVolumeSelection.setImageResource(R.drawable.btn_volume_selected);
        maEqualizerSelection.setImageResource(R.drawable.btn_equalizer_unselected);

        presetSpinner.setVisibility(View.GONE);
    }

    private void setEqualizerSelection() {
        setTitle(getResources().getString(R.string.title_2));

        maVolumeTopLayout.setVisibility(View.GONE);
        maVolumeBottomLayout.setVisibility(View.GONE);

        maEqualizerTopLayout.setVisibility(View.VISIBLE);
        maEqualizerBottomLayout.setVisibility(View.VISIBLE);

        equalizerCheckBox.setVisibility(View.VISIBLE);

        maVolumeSelection.setImageResource(R.drawable.btn_volume_unselected);
        maEqualizerSelection.setImageResource(R.drawable.btn_equalizer_selected);

        presetSpinner.setVisibility(View.VISIBLE);
    }

    //
    @Override
    public void progressChanged(int progress, long currentDuration, long totalDuration) {

    }

    @Override
    public void onCompletion() {
        maPlayImage.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void onStart(Track track) {
        maPlayImage.setImageResource(R.drawable.ic_pause);

        setTrackInformation(track);
    }

    @Override
    public void onPause(Track track) {
        maPlayImage.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public boolean onError(int what, int extra) {
        return false;
    }

    private void checkPermissions() {
        if (ExternalStorageUtil.isExternalStorageMounted()) {
            // If do not grant write external storage permission.
            if (ContextCompat.checkSelfPermission(EqoVolActivity.this, com.admanager.musicplayer.utilities.Constants.PERMISSIONS_STORAGE[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(EqoVolActivity.this, com.admanager.musicplayer.utilities.Constants.PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED) {

                // Request user to grant write external storage permission.
                ActivityCompat.requestPermissions(EqoVolActivity.this, com.admanager.musicplayer.utilities.Constants.PERMISSIONS_STORAGE, com.admanager.musicplayer.utilities.Constants.REQUEST_CODE_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSION);
            } else {
                gotoMusicPlayerActivity();
            }
        }
    }

    public enum MENU_COMPLETE {NONE, TUTORIAL, PRIVACY_POLICY}

    public class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            controlVolumeChange();
        }
    }
}