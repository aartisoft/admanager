package com.admanager.musicplayer.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.adapters.CustomFolderListAdapter;
import com.admanager.musicplayer.models.FileObject;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ExternalStorageUtil;
import com.admanager.musicplayer.utilities.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MPFolderActivity extends MPBaseMusicActivity {
    private static final int MUSIC_ACTIVITY_REQUEST_CODE = 1;
    private static final int FOLDER_ACTIVITY_REQUEST_CODE = 2;

    private TextView faTitleText, faWarningText;

    private CustomFolderListAdapter mfRecyclerViewAdapter;

    private FileObject fileObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_folder);

        faTitleText = findViewById(R.id.faTitleText);
        faWarningText = findViewById(R.id.faWarningText);

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        findViewById(R.id.faBackLayout).setOnClickListener(view -> finishActivityWithResult());

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view2 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view3 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view4 -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view5 -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view6 -> playNext());

        Bundle data = getIntent().getBundleExtra(Constants.BUNDLE_NAME);
        if (data != null) {
            fileObject = data.getParcelable(Constants.FILE_OBJECT);
            if (fileObject != null) {
                faTitleText.setText(fileObject.getName());
            }
        }

        doBindService();

        RecyclerView faRecyclerViewList = findViewById(R.id.faRecyclerViewThumbs);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager faRecyclerViewLayoutManager = new LinearLayoutManager(this);
        faRecyclerViewList.setLayoutManager(faRecyclerViewLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        faRecyclerViewList.addItemDecoration(itemDecoration);

        // specify an adapter (see also next example)
        mfRecyclerViewAdapter = new CustomFolderListAdapter(new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlFolderImageLayout) {
                showFolders(pos);
            } else if (viewId == R.id.mlFolderLayout) {
                showFolders(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                showFolders(pos);
            }
        });
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        loadFolders();
    }

    private void showFolders(int position) {
        FileObject currentFileObject = mfRecyclerViewAdapter.getItem(position);

        File file = new File(currentFileObject.getFilePath());

        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                Intent intent = new Intent(this, MPFolderActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.FILE_OBJECT, currentFileObject);

                intent.setExtrasClassLoader(FileObject.class.getClassLoader());
                intent.putExtra(Constants.BUNDLE_NAME, bundle);

                startActivityForResult(intent, FOLDER_ACTIVITY_REQUEST_CODE);
            } else {
                Track track = GetTracks.getTrack(this, file.getName());

                playTrackAction(track);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == MUSIC_ACTIVITY_REQUEST_CODE || requestCode == FOLDER_ACTIVITY_REQUEST_CODE) && resultCode == RESULT_OK) {
            controlTrack();
        }
    }

    private void showOptions(int position, View view) {
        FileObject currentFileObject = mfRecyclerViewAdapter.getItem(position);

        File file = new File(currentFileObject.getFilePath());

        if (file != null && file.exists()) {
            if (!file.isDirectory()) {
                Track track = GetTracks.getTrack(this, file.getName());

                if (track != null) {
                    if (track == null) return;

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(this, view);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.track_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(item -> {

                        if (item.getItemId() == R.id.action_play_now) {
                            playTrackAction(track);
                        } else if (item.getItemId() == R.id.action_play_next) {
                            playNext(track);
                        } else if (item.getItemId() == R.id.action_add_to_queue) {
                            addToQueue(track);
                        } else if (item.getItemId() == R.id.action_add_to_playlist) {
                            addToPlaylist(track);
                        }

                        return true;
                    });
                    //displaying the popup
                    popup.show();
                }
            }
        }
    }

    private void playTrackAction(Track track) {
        if (track == null) return;

        playTrack(track);

        gotoMusicActivity();
    }

    private void loadFolders() {
        AsyncTask.execute(this::loadFoldersAsyncTask);
    }

    private void loadFoldersAsyncTask() {
        List<FileObject> folderList = new ArrayList<>();

        if (fileObject == null) {
            //Internal Storage
            FileObject fileObjectInternalStorage = new FileObject();
            fileObjectInternalStorage.setFilePath(ExternalStorageUtil.getInternalStorage());
            fileObjectInternalStorage.setName(Constants.INTERNAL_STORAGE_NAME);

            folderList.add(fileObjectInternalStorage);

            //External Storage
            List<String> externalStorageList = ExternalStorageUtil.getExternalStorage();
            if (externalStorageList != null && externalStorageList.size() > 0) {
                for (int i = 0; i < externalStorageList.size(); i++) {
                    String filePath = externalStorageList.get(i);

                    File file = new File(filePath);
                    String[] list = file.list();

                    if (!("emulated".equals(file.getName()) || list == null || list.length == 0)) {
                        boolean addStorage = true;

                        for (int j = 0; j < folderList.size(); j++) {
                            FileObject addedFileObject = folderList.get(j);
                            String[] addedFileList = new File(addedFileObject.getFilePath()).list();

                            if (addedFileList != null && ExternalStorageUtil.equalsFileList(list, addedFileList)) {
                                addStorage = false;
                                break;
                            }
                        }

                        if (addStorage) {
                            FileObject fileObjectExternalStorage = new FileObject();
                            fileObjectExternalStorage.setFilePath(filePath);
                            fileObjectExternalStorage.setName(Constants.EXTERNAL_STORAGE_NAME + " " + (i + 1));

                            if (!fileObjectExternalStorage.getFilePath().equalsIgnoreCase(fileObjectInternalStorage.getFilePath())) {
                                folderList.add(fileObjectExternalStorage);
                            }
                        }
                    }
                }
            }

            if (folderList.size() == 1) {
                fileObject = folderList.get(0);

                runOnUiThread(() -> faTitleText.setText(fileObject.getName()));

                folderList = new ArrayList<>(getFolderList(fileObject));
            } else if (folderList.size() > 1) {
                runOnUiThread(() -> faTitleText.setText(Constants.ROOT_NAME));
            }
        } else {
            folderList.addAll(getFolderList(fileObject));
        }

        //Sort By Name

        List<FileObject> finalFolderList = folderList;

        runOnUiThread(() -> {

            if (finalFolderList != null && finalFolderList.size() > 0) {
                mfRecyclerViewAdapter.setData(finalFolderList);

                faWarningText.setVisibility(View.GONE);
            } else {
                faWarningText.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<FileObject> getFolderList(FileObject fileObject) {
        List<FileObject> folderList = new ArrayList<>();

        FileFilter filterDirectoriesOnly = file -> file.isDirectory() || Utils.isSoundFile(file);

        File[] folderFiles = new File(fileObject.getFilePath()).listFiles(filterDirectoriesOnly);

        //Sort By Name
        if (folderFiles != null && folderFiles.length > 1) {
            Arrays.sort(folderFiles, (object1, object2) -> object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase()));
        }

        if (folderFiles != null && folderFiles.length > 0) {
            for (File inFile : folderFiles) {

                FileObject fileObjectFolder = new FileObject();
                fileObjectFolder.setFilePath(inFile.getPath());
                fileObjectFolder.setName(inFile.getName());
                folderList.add(fileObjectFolder);
            }
        }

        return folderList;
    }
}
