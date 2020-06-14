package com.admanager.musicplayer.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.fragments.MPAlbumsFragment;
import com.admanager.musicplayer.fragments.MPArtistsFragment;
import com.admanager.musicplayer.fragments.MPGenresFragment;
import com.admanager.musicplayer.fragments.MPPlaylistsFragment;
import com.admanager.musicplayer.fragments.MPRecentFragment;
import com.admanager.musicplayer.fragments.MPTracksFragment;
import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.services.MediaPlayerService;
import com.admanager.musicplayer.tasks.GetTracks;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;
import com.google.android.material.tabs.TabLayout;

public class MPMainActivity extends MPBaseMusicActivity implements MPRecentFragment.OnFragmentInteractionListener,
        MPTracksFragment.OnFragmentInteractionListener, MPArtistsFragment.OnFragmentInteractionListener, MPAlbumsFragment.OnFragmentInteractionListener,
        MPGenresFragment.OnFragmentInteractionListener, MPPlaylistsFragment.OnFragmentInteractionListener {
    private static final int MUSIC_ACTIVITY_REQUEST_CODE = 1;
    private static final int ALBUM_ACTIVITY_REQUEST_CODE = 2;
    private static final int ARTIST_ACTIVITY_REQUEST_CODE = 3;
    private static final int GENRE_ACTIVITY_REQUEST_CODE = 4;
    private static final int PLAYLIST_ACTIVITY_REQUEST_CODE = 5;
    private static final int FOLDER_ACTIVITY_REQUEST_CODE = 6;
    private static boolean firstShow = false;
    private ViewPager viewPager;
    private SearchView searchView;
    private String query = "";

    private View toolbarSearch;
    private View toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_activity_main);

        maPlayImage = findViewById(R.id.maPlayImage);

        maTrackImage = findViewById(R.id.maTrackImage);
        maTitle = findViewById(R.id.maTitle);
        maArtist = findViewById(R.id.maArtist);

        findViewById(R.id.backImageLayout).setOnClickListener(view -> finishActivityWithResult());
        findViewById(R.id.refreshLayout).setOnClickListener(view -> refreshDatas());
        findViewById(R.id.searchLayout).setOnClickListener(view -> showSearchToolbar());
        findViewById(R.id.folderLayout).setOnClickListener(view -> gotoFolderActivity());

        findViewById(R.id.maTrackImageLayout).setOnClickListener(view -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maTitleArtistLayout).setOnClickListener(view2 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maEqualizerLayout).setOnClickListener(view3 -> gotoMusicActivity(Constants.MUSIC_TABS.MUSIC));
        findViewById(R.id.maPlayImageLayout).setOnClickListener(view4 -> playOrPause());
        findViewById(R.id.maPlayPreviousImageLayout).setOnClickListener(view5 -> playPrevious());
        findViewById(R.id.maPlayNextImageLayout).setOnClickListener(view6 -> playNext());

        doBindService();

        toolbar = findViewById(R.id.toolbar);

        toolbarSearch = findViewById(R.id.mptoolbarSearch);
        findViewById(R.id.maCloseSearch).setOnClickListener(v -> showToolbar());

        searchView = findViewById(R.id.maSearchView);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                hideKeyboard();

                search(query);

                //saRecyclerViewThumbs.requestFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (TextUtils.isEmpty(query)) {
                    this.onQueryTextSubmit("");
                }
                return false;
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);
        //viewPager.setSwipeable(false);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                PagerAdapter pagerAdapter = viewPager.getAdapter();
                if (pagerAdapter != null) {
                    refreshFragment(i);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //String tabTitles[] = new String[]{"Categories", "Popular Trending", "Live Wallpaper"};
        //add tab items with title..
        /*tabLayout.addTab(tabLayout.newTab().setText(tabTitles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[1]));
        tabLayout.addTab(tabLayout.newTab().setText(tabTitles[2]));*/
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(MediaPlayerService.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);

        if (SharedPrefUtils.getFirstStarted(this)) {
            SharedPrefUtils.setFirstStarted(this, false);

            GetTracks.refreshDatas(this);
        }

        if (SharedPrefUtils.getFirstActionView(this)) {
            actionUri = getIntent().getParcelableExtra(Constants.ACTION_URI_NAME);

            Track track = GetTracks.getTrack(this, actionUri);

            if (track != null && playTrack(track)) {
                SharedPrefUtils.setFirstActionView(this, false);

                gotoMusicActivityWithAction();
            }
        }
    }

    private void refreshFragment(int i) {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            Fragment fragment = (Fragment) pagerAdapter.instantiateItem(viewPager, i);

            if (i == 0) {
                ((MPRecentFragment) fragment).refresh(query);
            } else if (i == 1) {
                ((MPTracksFragment) fragment).refresh(query);
            } else if (i == 2) {
                ((MPArtistsFragment) fragment).refresh(query);
            } else if (i == 3) {
                ((MPAlbumsFragment) fragment).refresh(query);
            } else if (i == 4) {
                ((MPGenresFragment) fragment).refresh(query);
            } else if (i == 5) {
                ((MPPlaylistsFragment) fragment).refresh(query);
            }
        }
    }

    private void search(String queryString) {
        this.query = queryString;

        refreshCurrentFragment();
    }

    private void refreshCurrentFragment() {
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        if (pagerAdapter != null) {
            int currentItem = viewPager.getCurrentItem();

            refreshFragment(currentItem);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSearchToolbar() {
        toolbar.setVisibility(View.GONE);
        toolbarSearch.setVisibility(View.VISIBLE);

        if (!firstShow) {
            searchView.setFocusable(true);
            searchView.requestFocusFromTouch();

            firstShow = true;
        }

        searchView.requestFocus();
    }

    private void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        toolbarSearch.setVisibility(View.GONE);
    }

    public void gotoAlbumActivity(Track track) {
        Intent intent = new Intent(this, MPAlbumActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.TRACK_OBJECT, track);

        intent.setExtrasClassLoader(Track.class.getClassLoader());
        intent.putExtra(Constants.BUNDLE_NAME, bundle);

        startActivityForResult(intent, ALBUM_ACTIVITY_REQUEST_CODE);
    }

    public void gotoArtistActivity(Track track) {
        Intent intent = new Intent(this, MPArtistActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.TRACK_OBJECT, track);

        intent.setExtrasClassLoader(Track.class.getClassLoader());
        intent.putExtra(Constants.BUNDLE_NAME, bundle);

        startActivityForResult(intent, ARTIST_ACTIVITY_REQUEST_CODE);
    }

    public void gotoGenreActivity(Genre genre) {
        Intent intent = new Intent(this, MPGenreActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.GENRE_OBJECT, genre);

        intent.setExtrasClassLoader(Track.class.getClassLoader());
        intent.putExtra(Constants.BUNDLE_NAME, bundle);

        startActivityForResult(intent, GENRE_ACTIVITY_REQUEST_CODE);
    }

    public void gotoPlaylistActivity(Playlist playlist) {
        Intent intent = new Intent(this, MPPlaylistActivity.class);

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PLAYLIST_OBJECT, playlist);

        intent.setExtrasClassLoader(Playlist.class.getClassLoader());
        intent.putExtra(Constants.BUNDLE_NAME, bundle);

        startActivityForResult(intent, PLAYLIST_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == MUSIC_ACTIVITY_REQUEST_CODE || requestCode == ALBUM_ACTIVITY_REQUEST_CODE ||
                requestCode == ARTIST_ACTIVITY_REQUEST_CODE || requestCode == GENRE_ACTIVITY_REQUEST_CODE ||
                requestCode == PLAYLIST_ACTIVITY_REQUEST_CODE || requestCode == FOLDER_ACTIVITY_REQUEST_CODE) && resultCode == RESULT_OK) {
            controlTrack();

            refreshCurrentFragment();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayerService != null) mediaPlayerService.saveToCurrentQueue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayerService != null) mediaPlayerService.saveToCurrentQueue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_player_main, menu);
        return true;
    }

    private void refreshDatas() {
        GetTracks.refreshDatas(this);

        if (ContextUtils.isContextValid(this)) {
            Toast toast = Toast.makeText(this, getString(R.string.mp_music_list_refreshed), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void gotoFolderActivity() {
        Intent intent = new Intent(this, MPFolderActivity.class);

        startActivityForResult(intent, FOLDER_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        finishActivityWithResult();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles = {"Recent", "Tracks", "Artists", "Albums", "Genres", "Playlists"};

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) return MPRecentFragment.newInstance(query);
            else if (position == 1) return MPTracksFragment.newInstance(query);
            else if (position == 2) return MPArtistsFragment.newInstance(query);
            else if (position == 3) return MPAlbumsFragment.newInstance(query);
            else if (position == 4) return MPGenresFragment.newInstance(query);
            else if (position == 5) return MPPlaylistsFragment.newInstance(query);
            else return MPRecentFragment.newInstance(query);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
            // sadece icon istiyorsak return null yapmak yeterli
        }
    }


}
