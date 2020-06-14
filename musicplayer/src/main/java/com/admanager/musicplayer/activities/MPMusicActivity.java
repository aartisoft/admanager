package com.admanager.musicplayer.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.adapters.CustomQueueListAdapter;
import com.admanager.musicplayer.listeners.MPIPlayer;
import com.admanager.musicplayer.models.Preset;
import com.admanager.musicplayer.models.SpinnerPreset;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.services.MediaPlayerService;
import com.admanager.musicplayer.tasks.BassBoostView;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.tasks.LedView;
import com.admanager.musicplayer.tasks.NeedleRoundView;
import com.admanager.musicplayer.tasks.RingRoundView;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.CustomSpinnerAdapter;
import com.admanager.musicplayer.utilities.DynamicWidthSpinner;
import com.admanager.musicplayer.utilities.MediaUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;
import com.admanager.musicplayer.utilities.VerticalSeekBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class MPMusicActivity extends MPBaseActivity implements MPIPlayer, AdapterView.OnItemSelectedListener {
    private static final int REQUEST_PERMISSION_RECORD_AUDIO = 11;

    private static final int LEVEL_MUTE = 0;
    private static final int LEVEL_20 = 20;
    private static final int LEVEL_40 = 40;
    private static final int LEVEL_60 = 60;
    private static final int LEVEL_80 = 80;
    private static final int LEVEL_100 = 100;

    private ImageView maPlayImage;

    private ImageView maTrackImage;
    private TextView maTitle, maArtist;

    // To invoke the bound service, first make sure that this value
    // is not null.
    private MediaPlayerService mediaPlayerService;
    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private boolean mShouldUnbind;

    private SeekBar maSeekBar;
    private TextView maCurrentMsec, maTotalMsec;

    private FrameLayout maTrackListFrame, maEqualizerFrame, maBassBoostFrame, maVolumeFrame;
    private ImageView maTrackListImageOpen, maEqualizerImageOpen, maBassBoostImageOpen, maVolumeImageOpen;

    private ImageView maReplayImage, maShuffleImage;

    private RecyclerView maRecyclerViewList;
    private CustomQueueListAdapter maRecyclerViewAdapter;
    private LinearLayoutManager maRecyclerViewLayoutManager;

    private CustomSpinnerAdapter equalizerPresetSpinnerAdapter;
    private DynamicWidthSpinner presetSpinner;

    private VerticalSeekBar[] seekBars;

    private short lowerEqualizerBandLevel;

    private ImageView maBassBoostOnOffImage;
    private boolean bassBoostOpen = false;
    private BassBoostView maBassBoostView;
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mediaPlayerService = ((MediaPlayerService.LocalBinder) service).getService();

            mediaPlayerService.addPlayerListener(MPMusicActivity.this);

            controlTrackAction();

            loadTracks();

            if (equalizerPresetSpinnerAdapter != null) {
                selectSpinnerDefaultValue(equalizerPresetSpinnerAdapter.getSpinnerPresetArrayList());
            }

            loadBassBosst();
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
    private View maEditPresetLayout, maDeletePresetLayout, maOptionLayout;
    private SpinnerPreset selectedSpinnerPreset;
    //This variable holds only equalizer presets, not custom presets
    private ArrayList<SpinnerPreset> equalizerSpinnerPresetArrayList = new ArrayList<>();
    private Constants.MUSIC_TABS music_tabs;
    private AppCompatSeekBar seekBarVolume;
    private AudioManager mAudioManager;
    private TextView vfVolumeMuteButton, vfVolume20Button, vfVolume40Button, vfVolume60Button, vfVolume80Button, vfVolume100Button;
    private int streamMaxVolume;
    //Between 0-100
    private int volumeLevel;
    private NeedleRoundView needleRoundView;
    private RingRoundView ringRoundView;
    private LedView vfLeftLedView;
    private LedView vfRightLedView;
    private boolean seekbarTouched = true;
    private Handler m_Handler;
    private final Runnable m_Runnable = () -> {
        setLevel();
        startHandler();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_music);

        m_Handler = new Handler();

        music_tabs = (Constants.MUSIC_TABS) getIntent().getSerializableExtra(Constants.MUSIC_TABS_NAME);
        if (music_tabs == null) {
            music_tabs = Constants.MUSIC_TABS.MUSIC;
        }

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        maTrackListFrame = findViewById(R.id.maTrackListFrame);
        maEqualizerFrame = findViewById(R.id.maEqualizerFrame);
        maBassBoostFrame = findViewById(R.id.maBassBoostFrame);
        maVolumeFrame = findViewById(R.id.maVolumeFrame);

        maTrackListImageOpen = findViewById(R.id.maTrackListOpen);
        maEqualizerImageOpen = findViewById(R.id.maEqualizerOpen);
        maBassBoostImageOpen = findViewById(R.id.maBassBoostOpen);
        maVolumeImageOpen = findViewById(R.id.maVolumeOpen);

        maReplayImage = findViewById(R.id.maReplayImage);
        maShuffleImage = findViewById(R.id.maShuffleImage);

        findViewById(R.id.maTrackListLayout).setOnClickListener(view -> showTrackListView());
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view -> showEqualizerView());
        findViewById(R.id.maBassBoostLayout).setOnClickListener(view -> showBassBosstView());
        findViewById(R.id.maVolumeLayout).setOnClickListener(view -> showVolumeView());

        findViewById(R.id.maOptionLayout).setOnClickListener(this::showOptions);

        setReplayImage();
        setShuffleImage();

        maSeekBar = findViewById(R.id.maSeekBar);
        maSeekBar.setProgress(0);
        maSeekBar.setMax(100);
        maSeekBar.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.maSeekBar) {
                // Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position
                if (mediaPlayerService != null) {
                    SeekBar sb = (SeekBar) v;
                    Track track = mediaPlayerService.getTrack();
                    if (track != null) {
                        int playPositionInMilliSeconds = (int) (track.getDuration() / 100) * sb.getProgress();
                        mediaPlayerService.seekTo(playPositionInMilliSeconds);
                    }
                }
            }
            return false;
        });

        maCurrentMsec = findViewById(R.id.maCurrentMsec);
        maTotalMsec = findViewById(R.id.maTotalMsec);

        findViewById(R.id.maPlayLayout).setOnClickListener(view -> playOrPauseAction());
        findViewById(R.id.maPreviousLayout).setOnClickListener(view -> playPreviousAction());
        findViewById(R.id.maNextLayout).setOnClickListener(view -> playNextAction());

        findViewById(R.id.maReplayLayout).setOnClickListener(view -> changeReplay());
        findViewById(R.id.maShuffleLayout).setOnClickListener(view -> changeShuffle());

        maRecyclerViewList = findViewById(R.id.maRecyclerViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        maRecyclerViewLayoutManager = new LinearLayoutManager(this);
        maRecyclerViewList.setLayoutManager(maRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        maRecyclerViewList.addItemDecoration(itemDecoration);

        // specify an adapter (see also next example)
        maRecyclerViewAdapter = new CustomQueueListAdapter(new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlTrackNoLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlTrackImageLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlTitleLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlTotalMsecLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                playTrack(pos);
            }
        });
        maRecyclerViewList.setAdapter(maRecyclerViewAdapter);

        doBindService();

        //************************************************************************************* EQUALIZER ************************************************************************************/
        maEditPresetLayout = findViewById(R.id.maEditPresetLayout);
        maDeletePresetLayout = findViewById(R.id.maDeletePresetLayout);
        maOptionLayout = findViewById(R.id.maOptionLayout);

        maEditPresetLayout.setOnClickListener(view -> editPresetAction());
        maDeletePresetLayout.setOnClickListener(view -> deletePresetAction());

        presetSpinner = findViewById(R.id.efPresetSpinner);
        presetSpinner.setOnItemSelectedEvenIfUnchangedListener(this);

        findViewById(R.id.maCreatePresetLayout).setOnClickListener(view -> createPreset());

        //Vertical Seek Bars
        seekBars = new VerticalSeekBar[5];
        seekBars[0] = findViewById(R.id.efSeekbar_1);
        seekBars[1] = findViewById(R.id.efSeekbar_2);
        seekBars[2] = findViewById(R.id.efSeekbar_3);
        seekBars[3] = findViewById(R.id.efSeekbar_4);
        seekBars[4] = findViewById(R.id.efSeekbar_5);

        //Vertical Seek Bars Title
        TextView[] seekbar_Titles = new TextView[5];
        seekbar_Titles[0] = findViewById(R.id.efSeekbar_1_Title);
        seekbar_Titles[1] = findViewById(R.id.efSeekbar_2_Title);
        seekbar_Titles[2] = findViewById(R.id.efSeekbar_3_Title);
        seekbar_Titles[3] = findViewById(R.id.efSeekbar_4_Title);
        seekbar_Titles[4] = findViewById(R.id.efSeekbar_5_Title);

        Equalizer equalizerTemp = null;
        try {
            equalizerTemp = new Equalizer(Integer.MAX_VALUE - 1, 0);
            equalizerTemp.setEnabled(true);
            lowerEqualizerBandLevel = equalizerTemp.getBandLevelRange()[0];
            short upperEqualizerBandLevel = equalizerTemp.getBandLevelRange()[1];

            for (short i = 0; i < 5; i++) {
                final short equalizerBandIndex = i;
                seekBars[i].setId(i);
                seekBars[i].setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                seekBars[i].setProgress(equalizerTemp.getBandLevel(equalizerBandIndex));

                seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        updateCustomPreset();

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

            ArrayList<SpinnerPreset> spinnerPresetArrayList = new ArrayList<>();

            equalizerSpinnerPresetArrayList = new ArrayList<>();
            for (short i = 0; i < equalizerTemp.getNumberOfPresets(); i++) {
                SpinnerPreset spinnerPreset = new SpinnerPreset();
                spinnerPreset.setPresetNumber(i);
                spinnerPreset.setPresetName(equalizerTemp.getPresetName(i));
                spinnerPreset.setPosition(i);

                equalizerSpinnerPresetArrayList.add(spinnerPreset);
            }

            if (equalizerSpinnerPresetArrayList.size() > 0) {
                spinnerPresetArrayList.addAll(equalizerSpinnerPresetArrayList);
            }

            int spinnerIndex = spinnerPresetArrayList.size();

            ArrayList<Preset> presetList = SharedPrefUtils.getPresetArrayList(this);
            if (presetList == null || presetList.size() == 0) {
                Preset preset = addCustomPreset();
                preset.setPosition(spinnerIndex);

                spinnerPresetArrayList.add(preset);
            } else /*if(presetList != null && presetList.size() > 0)*/ {
                if (!existCustomPreset(presetList)) {
                    presetList.add(addCustomPreset());
                }

                for (SpinnerPreset spinnerPreset : presetList) {
                    spinnerPreset.setPosition(spinnerIndex);

                    spinnerIndex++;
                }
                spinnerPresetArrayList.addAll(presetList);
            }

            if (ContextUtils.isContextValid(this)) {
                equalizerPresetSpinnerAdapter = new CustomSpinnerAdapter(this, spinnerPresetArrayList);

                equalizerPresetSpinnerAdapter.setShowFirstItemEmpty(false);

                //equalizerPresetSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (equalizerTemp != null) {
                equalizerTemp.release();
            }
        }

        //Booster
        maBassBoostOnOffImage = findViewById(R.id.maBassBoostOnOffImage);
        maBassBoostOnOffImage.setOnClickListener(view -> changeBassBoost());

        maBassBoostView = findViewById(R.id.maBassBoostView);
        maBassBoostView.setMPBassBoostViewListener(this::bassBoostChanged);
        if (mediaPlayerService != null) {
            maBassBoostView.setPlaying(mediaPlayerService.isPlaying());
        }

        //Music tabs
        if (music_tabs == Constants.MUSIC_TABS.MUSIC) {
            showTrackListView();
        } else if (music_tabs == Constants.MUSIC_TABS.EQUALIZER) {
            showEqualizerView();
        } else if (music_tabs == Constants.MUSIC_TABS.BASS_BOOST) {
            showBassBosstView();
        } else if (music_tabs == Constants.MUSIC_TABS.VOLUME) {
            showVolumeView();
        }

        //Volume Frame
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        vfVolumeMuteButton = findViewById(R.id.vfVolumeMuteButton);
        vfVolumeMuteButton.setOnClickListener(v -> setVolume(LEVEL_MUTE, true));

        vfVolume20Button = findViewById(R.id.vfVolume20Button);
        vfVolume20Button.setOnClickListener(v -> setVolume(LEVEL_20, true));

        vfVolume40Button = findViewById(R.id.vfVolume40Button);
        vfVolume40Button.setOnClickListener(v -> setVolume(LEVEL_40, true));

        vfVolume60Button = findViewById(R.id.vfVolume60Button);
        vfVolume60Button.setOnClickListener(v -> setVolume(LEVEL_60, true));

        vfVolume80Button = findViewById(R.id.vfVolume80Button);
        vfVolume80Button.setOnClickListener(v -> setVolume(LEVEL_80, true));

        vfVolume100Button = findViewById(R.id.vfVolume100Button);
        vfVolume100Button.setOnClickListener(v -> setVolume(LEVEL_100, true));

        seekBarVolume = findViewById(R.id.volumeSeekbar);
        seekBarVolume.setMax(100);
        if (mAudioManager != null) {
            //seekBarVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int initProgress = (int) ((mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0f / streamMaxVolume) * 100.0f);

            seekbarTouched = false;
            seekBarVolume.setProgress(initProgress);

            this.volumeLevel = initProgress;

            controlVolumeButtons(volumeLevel);
        }

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                if (mAudioManager != null && seekbarTouched) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) ((progress / 100.0f) * (streamMaxVolume * 1.0f)), 0);  // 0 can also be changed to AudioManager.FLAG_PLAY_SOUND

                    volumeLevel = progress;

                    float degree = ((volumeLevel) / 100.0f) * (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f + NeedleRoundView.MIN_DEGREE;
                    needleRoundView.setDegree(degree);
                    ringRoundView.setDegree(degree);

                    controlVolumeButtons(progress);
                }

                seekbarTouched = true;
            }
        });

        needleRoundView = findViewById(R.id.vfNeedleRoundView);
        needleRoundView.getParent().requestDisallowInterceptTouchEvent(true);
        needleRoundView.setMPNeedleChangedListener(this::adjustVolume);

        ringRoundView = findViewById(R.id.vfRingRoundView);

        float degree = ((volumeLevel) / 100.0f) * (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f + NeedleRoundView.MIN_DEGREE;
        needleRoundView.setDegree(degree);
        ringRoundView.setDegree(degree);

        //LedView
        vfLeftLedView = findViewById(R.id.vfLeftLedView);
        vfRightLedView = findViewById(R.id.vfRightLedView);
    }

    private void adjustVolume(float degree) {
        if (degree >= NeedleRoundView.MIN_DEGREE && degree <= NeedleRoundView.MAX_DEGREE) {
            this.volumeLevel = (int) (((degree - NeedleRoundView.MIN_DEGREE) * 1.0f / (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f) * 100.0f);

            setVolume(volumeLevel, false);
        }
    }

    private void setVolume(int volumeLevel, boolean buttonClick) {
        this.volumeLevel = volumeLevel;

        if (this.volumeLevel <= 100 && mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) ((volumeLevel / 100.0f) * (streamMaxVolume * 1.0f)), 0);  // 0 can also be changed to AudioManager.FLAG_PLAY_SOUND

            seekbarTouched = false;
            seekBarVolume.setProgress(volumeLevel);

            float degree = ((volumeLevel) / 100.0f) * (NeedleRoundView.MAX_DEGREE - NeedleRoundView.MIN_DEGREE) * 1.0f + NeedleRoundView.MIN_DEGREE;
            if (buttonClick) needleRoundView.setDegree(degree);
            ringRoundView.setDegree(degree);
        }

        controlVolumeButtons(volumeLevel);
    }

    private void controlVolumeButtons(int volumeLevel) {
        if (volumeLevel == LEVEL_MUTE)
            vfVolumeMuteButton.setBackgroundResource(R.drawable.mp_mute_btn_lighten);
        else if (volumeLevel == LEVEL_20)
            vfVolume20Button.setTextColor(getResources().getColor(R.color.mpvolume_button_active));
        else if (volumeLevel == LEVEL_40)
            vfVolume40Button.setTextColor(getResources().getColor(R.color.mpvolume_button_active));
        else if (volumeLevel == LEVEL_60)
            vfVolume60Button.setTextColor(getResources().getColor(R.color.mpvolume_button_active));
        else if (volumeLevel == LEVEL_80)
            vfVolume80Button.setTextColor(getResources().getColor(R.color.mpvolume_button_active));
        else if (volumeLevel == LEVEL_100)
            vfVolume100Button.setTextColor(getResources().getColor(R.color.mpvolume_button_active));

        if (volumeLevel != LEVEL_MUTE)
            vfVolumeMuteButton.setBackgroundResource(R.drawable.mp_mute_btn_disable);
        if (volumeLevel != LEVEL_20)
            vfVolume20Button.setTextColor(getResources().getColor(R.color.mpvolume_button_inactive));
        if (volumeLevel != LEVEL_40)
            vfVolume40Button.setTextColor(getResources().getColor(R.color.mpvolume_button_inactive));
        if (volumeLevel != LEVEL_60)
            vfVolume60Button.setTextColor(getResources().getColor(R.color.mpvolume_button_inactive));
        if (volumeLevel != LEVEL_80)
            vfVolume80Button.setTextColor(getResources().getColor(R.color.mpvolume_button_inactive));
        if (volumeLevel != LEVEL_100)
            vfVolume100Button.setTextColor(getResources().getColor(R.color.mpvolume_button_inactive));
    }

    private void reCreateSpinnerArrayList() {
        ArrayList<SpinnerPreset> spinnerPresetArrayList = new ArrayList<>();

        if (equalizerSpinnerPresetArrayList.size() > 0) {
            for (short i = 0; i < equalizerSpinnerPresetArrayList.size(); i++) {
                SpinnerPreset spinnerPreset = equalizerSpinnerPresetArrayList.get(i);
                spinnerPreset.setPosition(i);
            }

            spinnerPresetArrayList.addAll(equalizerSpinnerPresetArrayList);
        }

        int spinnerIndex = spinnerPresetArrayList.size();
        ArrayList<Preset> presetList = SharedPrefUtils.getPresetArrayList(this);
        if (presetList != null && presetList.size() > 0) {
            for (SpinnerPreset spinnerPreset : presetList) {
                spinnerPreset.setPosition(spinnerIndex);

                spinnerIndex++;
            }
            spinnerPresetArrayList.addAll(presetList);
        }

        if (ContextUtils.isContextValid(this)) {
            equalizerPresetSpinnerAdapter = new CustomSpinnerAdapter(this, spinnerPresetArrayList);

            equalizerPresetSpinnerAdapter.setShowFirstItemEmpty(false);

            //equalizerPresetSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
            presetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

            //Set the text color of the Spinner's selected view (not a drop down list view)
            selectSpinnerDefaultValue(spinnerPresetArrayList);
        }
    }

    private void createPreset() {
        if (ContextUtils.isContextValid(this)) {
            final EditText taskEditText = new EditText(this);
            taskEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            taskEditText.requestFocus();

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mp_create_a_preset))
                    .setMessage(getResources().getString(R.string.mp_enter_preset_name))
                    .setView(taskEditText)
                    .setPositiveButton(getResources().getString(R.string.mp_create), (dialog1, which) ->
                    {
                        String presetName = String.valueOf(taskEditText.getText());

                        createPreset(presetName);
                    })
                    .setNegativeButton(getResources().getString(R.string.mp_cancel), null)
                    .create();
            dialog.show();
        }
    }

    private void createPreset(String presetName) {
        Preset preset = new Preset();
        preset.setPresetName(presetName);

        List<Short> bandLevelList = new ArrayList<>();

        for (short i = 0; i < 5; i++) {
            bandLevelList.add((short) (seekBars[i].getProgress() + lowerEqualizerBandLevel));
        }

        preset.setBandLevels(bandLevelList);

        ArrayList<Preset> presetList = SharedPrefUtils.getPresetArrayList(this);
        presetList.add(preset);

        SharedPrefUtils.setPresetArrayList(this, presetList);

        reCreateSpinnerArrayList();

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_preset_is_created, presetName), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //bassLevel is between 0 and 100
    private void bassBoostChanged(int bassLevel) {
        if (bassBoostOpen) {
            if (bassLevel >= 0 && bassLevel <= 100) {
                mediaPlayerService.setBassBoostStrength((short) (bassLevel * 10));

                SharedPrefUtils.setBassBoostLevel(this, bassLevel);
            }
        }
    }

    private void changeBassBoost() {
        if (bassBoostOpen) {
            closeBassBoost();
        } else {
            openBassBoost();
        }
    }

    private void closeBassBoost() {
        bassBoostOpen = false;
        SharedPrefUtils.setBassBoostOpen(this, false);

        maBassBoostOnOffImage.setImageResource(R.drawable.mp_booster_off);
        maBassBoostView.setEnabled(false);

        maBassBoostView.setBassLevel(0);

        if (mediaPlayerService != null) mediaPlayerService.setBassBoostStrength((short) 0);
    }

    private void openBassBoost() {
        bassBoostOpen = true;
        SharedPrefUtils.setBassBoostOpen(this, true);

        maBassBoostOnOffImage.setImageResource(R.drawable.mp_booster_on);
        maBassBoostView.setEnabled(true);

        int bassBoostLevel = SharedPrefUtils.getBassBoostLevel(this);
        maBassBoostView.setBassLevel(bassBoostLevel);

        if (mediaPlayerService != null)
            mediaPlayerService.setBassBoostStrength((short) (bassBoostLevel * 10));
    }

    private void loadBassBosst() {
        bassBoostOpen = SharedPrefUtils.getBassBoostOpen(this);

        if (bassBoostOpen) {
            openBassBoost();
        } else {
            closeBassBoost();
        }
    }

    private void showTrackListView() {
        music_tabs = Constants.MUSIC_TABS.MUSIC;

        maTrackListFrame.setVisibility(View.VISIBLE);
        maEqualizerFrame.setVisibility(View.GONE);
        maBassBoostFrame.setVisibility(View.GONE);
        maVolumeFrame.setVisibility(View.GONE);

        maTrackListImageOpen.setImageResource(R.drawable.mp_ic_view_active);
        maEqualizerImageOpen.setImageResource(R.drawable.mp_ic_equalizer_passive);
        maBassBoostImageOpen.setImageResource(R.drawable.mp_ic_booster_passive);
        maVolumeImageOpen.setImageResource(R.drawable.mp_ic_volume_passive);

        maEditPresetLayout.setVisibility(View.GONE);
        maDeletePresetLayout.setVisibility(View.GONE);
        maOptionLayout.setVisibility(View.VISIBLE);
    }

    private void showEqualizerView() {
        music_tabs = Constants.MUSIC_TABS.EQUALIZER;

        maTrackListFrame.setVisibility(View.GONE);
        maEqualizerFrame.setVisibility(View.VISIBLE);
        maBassBoostFrame.setVisibility(View.GONE);
        maVolumeFrame.setVisibility(View.GONE);

        maTrackListImageOpen.setImageResource(R.drawable.mp_ic_view_passive);
        maEqualizerImageOpen.setImageResource(R.drawable.mp_ic_equalizer_active);
        maBassBoostImageOpen.setImageResource(R.drawable.mp_ic_booster_passive);
        maVolumeImageOpen.setImageResource(R.drawable.mp_ic_volume_passive);

        setVisibilityForEditAndDeletePreset(selectedSpinnerPreset);
        maOptionLayout.setVisibility(View.GONE);
    }

    private void setVisibilityForEditAndDeletePreset(SpinnerPreset selectedSpinnerPreset) {
        if (selectedSpinnerPreset instanceof Preset) {
            if (selectedSpinnerPreset.getPresetName().toLowerCase().equalsIgnoreCase(Constants.CUSTOM_PRESET_NAME.toLowerCase())) {
                maEditPresetLayout.setVisibility(View.GONE);
                maDeletePresetLayout.setVisibility(View.GONE);
            } else {
                maEditPresetLayout.setVisibility(View.VISIBLE);
                maDeletePresetLayout.setVisibility(View.VISIBLE);
            }
        } else {
            maEditPresetLayout.setVisibility(View.GONE);
            maDeletePresetLayout.setVisibility(View.GONE);
        }
    }

    private void showBassBosstView() {
        music_tabs = Constants.MUSIC_TABS.BASS_BOOST;

        maTrackListFrame.setVisibility(View.GONE);
        maEqualizerFrame.setVisibility(View.GONE);
        maBassBoostFrame.setVisibility(View.VISIBLE);
        maVolumeFrame.setVisibility(View.GONE);

        maTrackListImageOpen.setImageResource(R.drawable.mp_ic_view_passive);
        maEqualizerImageOpen.setImageResource(R.drawable.mp_ic_equalizer_passive);
        maBassBoostImageOpen.setImageResource(R.drawable.mp_ic_booster_active);
        maVolumeImageOpen.setImageResource(R.drawable.mp_ic_volume_passive);

        maEditPresetLayout.setVisibility(View.GONE);
        maDeletePresetLayout.setVisibility(View.GONE);
        maOptionLayout.setVisibility(View.GONE);
    }

    private void showVolumeView() {
        music_tabs = Constants.MUSIC_TABS.VOLUME;

        maTrackListFrame.setVisibility(View.GONE);
        maEqualizerFrame.setVisibility(View.GONE);
        maBassBoostFrame.setVisibility(View.GONE);
        maVolumeFrame.setVisibility(View.VISIBLE);

        maTrackListImageOpen.setImageResource(R.drawable.mp_ic_view_passive);
        maEqualizerImageOpen.setImageResource(R.drawable.mp_ic_equalizer_passive);
        maBassBoostImageOpen.setImageResource(R.drawable.mp_ic_booster_passive);
        maVolumeImageOpen.setImageResource(R.drawable.mp_ic_volume_active);

        maEditPresetLayout.setVisibility(View.GONE);
        maDeletePresetLayout.setVisibility(View.GONE);
        maOptionLayout.setVisibility(View.GONE);

        adjustVisualizer();
        startHandler();

    }

    private void changeReplay() {
        Constants.REPLAY_TYPE replay_type = SharedPrefUtils.getReplay(this);

        if (replay_type == Constants.REPLAY_TYPE.NO_REPLAY) {
            replay_type = Constants.REPLAY_TYPE.REPLAY_ALL;
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ALL) {
            replay_type = Constants.REPLAY_TYPE.REPLAY_ONE;
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ONE) {
            replay_type = Constants.REPLAY_TYPE.NO_REPLAY;
        }

        SharedPrefUtils.setReplay(this, replay_type);

        setReplayImage();
    }

    private void setReplayImage() {
        Constants.REPLAY_TYPE replay_type = SharedPrefUtils.getReplay(this);

        if (replay_type == Constants.REPLAY_TYPE.NO_REPLAY) {
            maReplayImage.setImageResource(R.drawable.mp_ic_replay_passive);
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ALL) {
            maReplayImage.setImageResource(R.drawable.mp_ic_replay_all);
        } else if (replay_type == Constants.REPLAY_TYPE.REPLAY_ONE) {
            maReplayImage.setImageResource(R.drawable.mp_ic_replay_one);
        }
    }

    private void changeShuffle() {
        boolean shuffle = SharedPrefUtils.getShuffle(this);

        if (shuffle) {
            SharedPrefUtils.setShuffle(this, false);

            mediaPlayerService.setShuffle(false);

            maRecyclerViewAdapter.clear();
            maRecyclerViewAdapter.setData(mediaPlayerService.getTrackArrayList());
        } else {
            SharedPrefUtils.setShuffle(this, true);

            mediaPlayerService.saveToOriginalQueue();

            mediaPlayerService.setShuffle(true);

            maRecyclerViewAdapter.clear();
            maRecyclerViewAdapter.setData(mediaPlayerService.getTrackArrayList());
        }

        setShuffleImage();
    }

    private void setShuffleImage() {
        boolean shuffle = SharedPrefUtils.getShuffle(this);

        if (shuffle) {
            maShuffleImage.setImageResource(R.drawable.mp_ic_shuffle_active);
        } else {
            maShuffleImage.setImageResource(R.drawable.mp_ic_shuffle_passive);
        }
    }

    private void playTrack(int position) {
        if (position < 0) return;

        Track track = maRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        mediaPlayerService.play(position);

        maPlayImage.setImageResource(R.drawable.mp_ic_stop);

        maRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void loadTracks() {
        if (mediaPlayerService == null || mediaPlayerService.getTrackArrayList() == null) return;

        AsyncTask.execute(this::loadTracksAsyncTask);
    }

    private void loadTracksAsyncTask() {
        if (mediaPlayerService == null) return;

        List<Track> trackList = mediaPlayerService.getTrackArrayList();

        if (trackList == null || trackList.size() == 0) return;

        int lastPlayingTrackPosition = mediaPlayerService.getIndex();

        runOnUiThread(() -> {

            maRecyclerViewAdapter.setData(trackList, lastPlayingTrackPosition);

        });
    }

    private void playOrPauseAction() {
        if (mediaPlayerService == null) return;

        if (mediaPlayerService.isPlaying()) {
            mediaPlayerService.pause();

            maRecyclerViewAdapter.notifyDataSetChanged();

            maPlayImage.setImageResource(R.drawable.mp_ic_play_passive);
        } else {
            if (mediaPlayerService.getTrack() != null) {
                mediaPlayerService.play();

                maPlayImage.setImageResource(R.drawable.mp_ic_stop);
            }
        }
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
                maPlayImage.setImageResource(R.drawable.mp_ic_stop);
            } else {
                maPlayImage.setImageResource(R.drawable.mp_ic_play_passive);
            }

            maBassBoostView.setPlaying(mediaPlayerService.isPlaying());
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

    @Override
    protected void onPause() {
        super.onPause();

        stopHandler();

        if (mediaPlayerService != null) mediaPlayerService.saveToCurrentQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startHandler();
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

    private void setLevel() {
        if (music_tabs != Constants.MUSIC_TABS.VOLUME || mediaPlayerService == null) return;

        int level = mediaPlayerService.getLevelFromVisualizer();

        vfRightLedView.setLevelNumber(level);
        vfLeftLedView.setLevelNumber(level);
    }

    private void adjustVisualizer() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
        } else {
            adjustVisualizerYes();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_RECORD_AUDIO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adjustVisualizerYes();
            } else {
                if (ContextUtils.isContextValid(this)) {
                    Toast toast = Toast.makeText(MPMusicActivity.this, getResources().getString(R.string.mp_visualizer_permission_not_granted), Toast.LENGTH_LONG);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayerService != null) mediaPlayerService.saveToCurrentQueue();

        doUnbindService();

        if (mediaPlayerService != null) {
            mediaPlayerService.removePlayerListener(this);

            mediaPlayerService = null;
        }
    }

    @Override
    public void progressChanged(int progress, long currentDuration, long totalDuration) {
        maSeekBar.setProgress(progress);

        maCurrentMsec.setText(MediaUtils.getTimeWithFormatForMilliSecond(currentDuration));
    }

    @Override
    public void onStart(Track track) {
        setTrackInformation(track);

        maPlayImage.setImageResource(R.drawable.mp_ic_stop);

        maRecyclerViewAdapter.notifyDataSetChanged();

        maBassBoostView.setPlaying(true);

        scrollIfNecessary();
    }

    private void scrollIfNecessary() {
        int visibleItemCount = maRecyclerViewLayoutManager.getChildCount();
        if (visibleItemCount > 0) visibleItemCount--;

        int firstVisibleItemPosition = maRecyclerViewLayoutManager.findFirstVisibleItemPosition();

        int position = maRecyclerViewAdapter.getPlayingTrack();

        if (position >= 0) {
            if (position <= firstVisibleItemPosition) {
                maRecyclerViewList.scrollToPosition(position);
            } else if (position >= (firstVisibleItemPosition + visibleItemCount)) {
                maRecyclerViewList.scrollToPosition(position);
            }
        }
    }

    @Override
    public void onPause(Track track) {
        maPlayImage.setImageResource(R.drawable.mp_ic_play_passive);

        maRecyclerViewAdapter.notifyDataSetChanged();

        maBassBoostView.setPlaying(false);
    }

    @Override
    public void onCompletion() {
        maPlayImage.setImageResource(R.drawable.mp_ic_play_passive);
        maSeekBar.setProgress(0);
        maCurrentMsec.setText(MediaUtils.getTimeWithFormatForMilliSecond(0));
        maRecyclerViewAdapter.notifyDataSetChanged();
        maBassBoostView.setPlaying(false);
    }

    @Override
    public void onBufferingUpdate(int percent) {
        maSeekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared() {
        //maTotalMsec.setText(MediaUtils.getTimeWithFormatForMilliSecond(mediaPlayerService.getMediaFileLengthInMilliseconds()));

        //dismissProgressDialog();
    }

    @Override
    public boolean onError(int what, int extra) {
        /*dismissProgressDialog();

        if(ContextUtils.isContextValid(this) && !showedErrorMessage)
        {
            showedErrorMessage = true;

            Toast toast = Toast.makeText(this, "An error occured!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }*/

        return false;
    }

    @Override
    public void onBackPressed() {
        finishActivityWithResult();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (equalizerPresetSpinnerAdapter != null) {
            selectedSpinnerPreset = equalizerPresetSpinnerAdapter.getItem(position);

            if (mediaPlayerService != null && selectedSpinnerPreset != null) {
                if (selectedSpinnerPreset instanceof Preset) {
                    List<Short> bandLevelList = ((Preset) selectedSpinnerPreset).getBandLevels();

                    for (short i = 0; i < 5; i++) {
                        seekBars[i].setProgress(bandLevelList.get(i) - lowerEqualizerBandLevel);
                    }
                } else {
                    try {
                        mediaPlayerService.equalizer.usePreset((short) selectedSpinnerPreset.getPresetNumber());
                    } catch (IllegalStateException | IllegalArgumentException | UnsupportedOperationException e) {
                        e.printStackTrace();
                    }

                    for (short i = 0; i < 5; i++) {
                        seekBars[i].setProgress(mediaPlayerService.equalizer.getBandLevel(i) - lowerEqualizerBandLevel);
                    }
                }
            }

            equalizerPresetSpinnerAdapter.setSelectedPosition(position);

            if (selectedSpinnerPreset != null) {
                if (music_tabs == Constants.MUSIC_TABS.EQUALIZER) {
                    setVisibilityForEditAndDeletePreset(selectedSpinnerPreset);
                }

                SharedPrefUtils.setPresetName(this, selectedSpinnerPreset.getPresetName());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void editPresetAction() {
        if (ContextUtils.isContextValid(this)) {
            String presetName = "";
            if (selectedSpinnerPreset != null) {
                Preset preset = (Preset) selectedSpinnerPreset;
                presetName = preset.getPresetName();
            }
            String oldPresetName = presetName;

            final EditText taskEditText = new EditText(this);
            taskEditText.setText(presetName);
            taskEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            taskEditText.requestFocus();

            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.mp_rename_preset))
                    .setMessage(getString(R.string.mp_enter_new_preset_name))
                    .setView(taskEditText)
                    .setPositiveButton(getResources().getString(R.string.mp_update), (dialog1, which) ->
                    {
                        String newPresetName = String.valueOf(taskEditText.getText());

                        editPresetActionYes(oldPresetName, newPresetName);
                    })
                    .setNegativeButton(getResources().getString(R.string.mp_cancel), null)
                    .create();
            dialog.show();
        }
    }

    private void editPresetActionYes(String oldPresetName, String newPresetName) {
        boolean edited = false;

        if (selectedSpinnerPreset != null) {
            Preset preset = (Preset) selectedSpinnerPreset;
            preset.setPresetName(newPresetName);

            ArrayList<Preset> presetList = SharedPrefUtils.getPresetArrayList(this);
            for (Preset preset1 : presetList) {
                if (oldPresetName.toLowerCase().equalsIgnoreCase(preset1.getPresetName().toLowerCase())) {
                    preset1.setPresetName(newPresetName);

                    SharedPrefUtils.setPresetArrayList(this, presetList);

                    edited = true;

                    equalizerPresetSpinnerAdapter.notifyDataSetChanged();

                    break;
                }
            }
        }

        if (edited) {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_preset_is_updated), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_preset_not_updated), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void deletePresetAction() {
        if (ContextUtils.isContextValid(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            String presetName = "";
            if (selectedSpinnerPreset != null) {
                Preset preset = (Preset) selectedSpinnerPreset;
                presetName = preset.getPresetName();
            }

            builder.setTitle(getResources().getString(R.string.mp_remove_preset));
            builder.setMessage(getString(R.string.mp_are_you_sure_to_remove_preset, presetName));

            builder.setPositiveButton(getResources().getString(R.string.mp_yes), (dialog, which) -> deletePresetActionYes());

            builder.setNegativeButton(getResources().getString(R.string.mp_no), (dialog, which) -> {

                if (dialog != null && ContextUtils.isContextValid(this)) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void deletePresetActionYes() {
        boolean deleted = false;

        Preset preset = null;

        if (selectedSpinnerPreset != null) {
            preset = (Preset) selectedSpinnerPreset;

            ArrayList<Preset> presetList = SharedPrefUtils.getPresetArrayList(this);

            if (preset != null && presetList != null && presetList.size() > 0) {
                for (Preset preset1 : presetList) {
                    if (preset.getPresetName() != null && preset1.getPresetName() != null && preset.getPresetName().toLowerCase().equalsIgnoreCase(preset1.getPresetName().toLowerCase())) {
                        presetList.remove(preset1);
                        deleted = true;

                        SharedPrefUtils.setPresetArrayList(this, presetList);

                        reCreateSpinnerArrayList();

                        break;
                    }
                }
            }
        }

        if (deleted) {
            if (ContextUtils.isContextValid(this) && preset != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_preset_is_deleted, preset.getPresetName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            if (ContextUtils.isContextValid(this) && preset != null) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_preset_is_not_deleted, preset.getPresetName()), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void selectSpinnerDefaultValue(List<SpinnerPreset> spinnerPresetArrayList) {
        int spinnerPresetIndex = getSpinnerPresetIndex(spinnerPresetArrayList);
        if (presetSpinner != null && spinnerPresetIndex >= 0) {
            presetSpinner.setSelection(spinnerPresetIndex);

            if (equalizerPresetSpinnerAdapter != null)
                equalizerPresetSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private void finishActivityWithResult() {
        Intent intent = new Intent();

        setResult(RESULT_OK, intent);

        finish();
    }

    private void showOptions(int position, View view) {
        if (position < 0) return;

        Track track = maRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.queue_track_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_now) {
                playTrack(position);
            } else if (item.getItemId() == R.id.action_move_to_first) {
                moveToFirst(position, track);
            } else if (item.getItemId() == R.id.action_move_to_last) {
                moveToLast(position, track);
            } else if (item.getItemId() == R.id.action_move_up) {
                moveUp(position, track);
            } else if (item.getItemId() == R.id.action_move_down) {
                moveDown(position, track);
            } else if (item.getItemId() == R.id.action_remove_from_queue) {
                removeTrack(position, track);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void moveToFirst(int position, Track track) {
        if (position < 1) return;
        if (track == null) return;

        if (maRecyclerViewAdapter.getItemCount() <= 1) return;

        if (mediaPlayerService == null) return;

        if (mediaPlayerService.moveToFirst(position)) {
            maRecyclerViewAdapter.moveToFirst(position);
        }
    }

    private void moveToLast(int position, Track track) {
        if (position < 0) return;
        if (track == null) return;

        if (maRecyclerViewAdapter.getItemCount() <= 1) return;
        if ((position + 1) == maRecyclerViewAdapter.getItemCount()) return;

        if (mediaPlayerService == null) return;

        if (mediaPlayerService.moveToLast(position)) {
            maRecyclerViewAdapter.moveToLast(position);
        }
    }

    private void moveUp(int position, Track track) {
        if (position < 1) return;
        if (track == null) return;

        if (maRecyclerViewAdapter.getItemCount() <= 1) return;

        if (mediaPlayerService == null) return;

        if (mediaPlayerService.moveUp(position)) {
            maRecyclerViewAdapter.moveUp(position);
        }
    }

    private void moveDown(int position, Track track) {
        if (position < 0) return;
        if (track == null) return;

        if (maRecyclerViewAdapter.getItemCount() <= 1) return;
        if ((position + 1) == maRecyclerViewAdapter.getItemCount()) return;

        if (mediaPlayerService == null) return;

        if (mediaPlayerService.moveDown(position)) {
            maRecyclerViewAdapter.moveDown(position);
        }
    }

    private void removeTrack(int position, Track track) {
        if (track == null) return;

        if (mediaPlayerService.removeTrack(track)) {
            maRecyclerViewAdapter.remove(position);
        }
    }

    private void setTrackInformation(Track track) {
        maTitle.setText(track.getTitle());

        maArtist.setText(track.getArtist());

        maTotalMsec.setText(MediaUtils.getTimeWithFormatForMilliSecond(track.getDuration()));

        String imagePath = GetTracks.getAlbumImagePath(this, track.getAlbumId());
        if (!TextUtils.isEmpty(imagePath)) {
            RequestOptions options = new RequestOptions();
            options = options.diskCacheStrategy(DiskCacheStrategy.ALL);
            options = options.centerCrop();
            options = options.dontAnimate();

            Glide.with(this)
                    .load(imagePath)
                    .apply(options)
                    .into(maTrackImage);
        } else {
            maTrackImage.setImageResource(R.drawable.mp_ic_music);
        }
    }

    private void updateCustomPreset() {
        ArrayList<Preset> spinnerPresetArrayList = SharedPrefUtils.getPresetArrayList(this);
        for (Preset preset : spinnerPresetArrayList) {
            if (preset.getPresetName().toLowerCase().equalsIgnoreCase(Constants.CUSTOM_PRESET_NAME.toLowerCase())) {
                List<Short> bandLevels = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    bandLevels.add((short) (seekBars[i].getProgress() + lowerEqualizerBandLevel));
                }
                preset.setBandLevels(bandLevels);
                SharedPrefUtils.setPresetArrayList(this, spinnerPresetArrayList);
                SharedPrefUtils.setPresetName(this, Constants.CUSTOM_PRESET_NAME);
            }
        }
    }

    private boolean existCustomPreset(ArrayList<Preset> presetList) {
        for (Preset preset : presetList) {
            if (preset.getPresetNumber() == Constants.CUSTOM_PRESET_NUMBER) {
                return true;
            }
        }

        return false;
    }

    private Preset addCustomPreset() {
        Preset preset = new Preset();
        preset.setPresetName(Constants.CUSTOM_PRESET_NAME);
        preset.setPresetNumber(Constants.CUSTOM_PRESET_NUMBER);

        List<Short> bandLevels = new ArrayList<>();
        for (short i = 0; i < 5; i++) {
            bandLevels.add((short) 0);
        }

        preset.setBandLevels(bandLevels);

        ArrayList<Preset> presetArrayList = SharedPrefUtils.getPresetArrayList(this);
        presetArrayList.add(preset);

        SharedPrefUtils.setPresetArrayList(this, presetArrayList);

        return preset;
    }

    private int getSpinnerPresetIndex(List<SpinnerPreset> spinnerPresetList) {
        String presetName = SharedPrefUtils.getPresetName(this);

        if (TextUtils.isEmpty(presetName)) return 0;

        if (spinnerPresetList == null || spinnerPresetList.size() == 0) return 0;

        int i = 0;
        for (SpinnerPreset spinnerPreset : spinnerPresetList) {
            if (spinnerPreset.getPresetName().toLowerCase().equalsIgnoreCase(presetName.toLowerCase())) {
                return i;
            }

            i++;
        }

        return 0;
    }

    private void showOptions(View view) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.music_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_remove_all) {
                removeAll();
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void removeAll() {
        if (ContextUtils.isContextValid(this) && maRecyclerViewAdapter.getItemCount() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getResources().getString(R.string.mp_remove_all_track));
            builder.setMessage(getResources().getString(R.string.mp_are_you_sure_to_remove_track));

            builder.setPositiveButton(getResources().getString(R.string.mp_yes), (dialog, which) -> removeAllYes());

            builder.setNegativeButton(getResources().getString(R.string.mp_no), (dialog, which) -> {

                if (dialog != null && ContextUtils.isContextValid(this)) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void removeAllYes() {
        if (mediaPlayerService.clear()) {
            maRecyclerViewAdapter.clear();

            if (ContextUtils.isContextValid(this)) {
                Toast toast = Toast.makeText(this, getString(R.string.mp_all_tracks_are_removed_from_queue), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
