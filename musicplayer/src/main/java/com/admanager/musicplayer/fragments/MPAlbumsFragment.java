package com.admanager.musicplayer.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.activities.MPBaseActivity;
import com.admanager.musicplayer.activities.MPBaseMusicActivity;
import com.admanager.musicplayer.activities.MPMainActivity;
import com.admanager.musicplayer.adapters.CustomAlbumsListAdapter;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.tasks.GetTracks;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MPAlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MPAlbumsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String query;

    private OnFragmentInteractionListener mListener;

    private CustomAlbumsListAdapter mfRecyclerViewAdapter;

    public MPAlbumsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MPAlbumsFragment newInstance(String param1) {
        MPAlbumsFragment fragment = new MPAlbumsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_PARAM1);
        }
    }

    public void refresh(String queryString) {
        query = queryString;

        loadAlbums();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mp_fragment_albums, container, false);
        // Inflate the layout for this fragment

        RecyclerView faRecyclerViewList = view.findViewById(R.id.afListViewMusic);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //faRecyclerViewList.setHasFixedSize(true);

        // use a linear layout manager
        if (getContext() != null) {
            RecyclerView.LayoutManager faRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
            faRecyclerViewList.setLayoutManager(faRecyclerViewLayoutManager);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            faRecyclerViewList.addItemDecoration(itemDecoration);
        }

        // specify an adapter (see also next example)
        mfRecyclerViewAdapter = new CustomAlbumsListAdapter(getContext(), new ArrayList<>(), (view2, pos) ->
        {
            long viewId = view2.getId();

            if (viewId == R.id.mlTrackImageLayout) {
                showAlbumTracks(pos);
            } else if (viewId == R.id.mlArtistLayout) {
                showAlbumTracks(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                showAlbumTracks(pos);
            }
        });
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadAlbums();
    }

    private void showAlbumTracks(int position) {
        if (position < 0) return;

        Track track = mfRecyclerViewAdapter.getItem(position);

        if (track != null && getActivity() != null) {
            ((MPMainActivity) getActivity()).gotoAlbumActivity(track);
        }
    }

    private void showOptions(int position, View view) {
        if (position < 0) return;

        Track track = mfRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.album_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_open) {
                showAlbumTracks(position);
            } else if (item.getItemId() == R.id.action_play_all) {
                playAll(track);
            } else if (item.getItemId() == R.id.action_add_all) {
                addAll(track);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addAlbumTracksToPlaylist(track);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void playAll(Track albumTrack) {
        if (albumTrack == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).playAlbumTracks(albumTrack);
        }
    }

    private void addAll(Track albumTrack) {
        if (albumTrack == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).addAlbumTracksToQueue(albumTrack);
        }
    }

    private void addAlbumTracksToPlaylist(Track albumTrack) {
        if (albumTrack == null) return;

        if (getActivity() != null) {
            ((MPBaseActivity) getActivity()).addAlbumTracksToPlaylist(albumTrack);
        }
    }

    private void loadAlbums() {
        AsyncTask.execute(this::loadAlbumsAsyncTask);
    }

    private void loadAlbumsAsyncTask() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mfRecyclerViewAdapter.clear());
        }

        List<Track> trackList = GetTracks.getAllAlbums(getContext(), query);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                if (trackList != null && trackList.size() > 0) {
                    mfRecyclerViewAdapter.setData(trackList);
                }
            });
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
