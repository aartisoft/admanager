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
import com.admanager.musicplayer.adapters.CustomTracksListAdapter;
import com.admanager.musicplayer.models.Track;
import com.admanager.musicplayer.utilities.Constants;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MPRecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MPRecentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String query;

    private OnFragmentInteractionListener mListener;

    private CustomTracksListAdapter mfRecyclerViewAdapter;

    public MPRecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RecentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MPRecentFragment newInstance(String param1) {
        MPRecentFragment fragment = new MPRecentFragment();
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

        loadRecentTracks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mp_fragment_recent, container, false);

        RecyclerView faRecyclerViewList = view.findViewById(R.id.rfListViewMusic);
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
        mfRecyclerViewAdapter = new CustomTracksListAdapter(getContext(), new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlTrackImageLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlArtistLayout) {
                playTrack(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                playTrack(pos);
            }
        });
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadRecentTracks();
    }

    private void showOptions(int position, View view) {
        if (position < 0) return;

        Track track = mfRecyclerViewAdapter.getItem(position);

        if (track == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.track_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_play_now) {
                playTrack(position);
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

    private void playNext(Track track) {
        if (track == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).playNext(track);
        }
    }

    private void addToQueue(Track track) {
        if (track == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).addToQueue(track);
        }
    }

    private void addToPlaylist(Track track) {
        if (track == null) return;

        if (getActivity() != null) {
            ((MPBaseActivity) getActivity()).addToPlaylist(track);
        }
    }


    private void loadRecentTracks() {
        AsyncTask.execute(this::loadRecentTracksAsyncTask);
    }

    private void loadRecentTracksAsyncTask() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mfRecyclerViewAdapter.clear());
        }

        List<Track> trackList = new ArrayList<>();

        if (getActivity() != null) {
            trackList = SharedPrefUtils.getRecentTrackArraySearchList(getActivity(), query);

            Collections.reverse(trackList);
        }

        List<Track> finalTrackList = trackList;

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                if (finalTrackList != null && finalTrackList.size() > 0) {
                    mfRecyclerViewAdapter.setData(finalTrackList);
                }
            });
        }
    }

    private void playTrack(int pos) {
        Track track = mfRecyclerViewAdapter.getItem(pos);

        if (track == null) return;

        if (getActivity() != null) {
            ((MPMainActivity) getActivity()).playTrack(track);

            ((MPMainActivity) getActivity()).gotoMusicActivity(Constants.MUSIC_TABS.MUSIC);
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
