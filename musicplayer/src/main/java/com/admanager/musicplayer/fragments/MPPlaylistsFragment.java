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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.musicplayer.R;
import com.admanager.musicplayer.activities.MPBaseActivity;
import com.admanager.musicplayer.activities.MPBaseMusicActivity;
import com.admanager.musicplayer.activities.MPMainActivity;
import com.admanager.musicplayer.adapters.CustomPlaylistsListAdapter;
import com.admanager.musicplayer.models.Playlist;
import com.admanager.musicplayer.utilities.ContextUtils;
import com.admanager.musicplayer.utilities.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MPPlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MPPlaylistsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String query;

    private OnFragmentInteractionListener mListener;

    private CustomPlaylistsListAdapter mfRecyclerViewAdapter;

    public MPPlaylistsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PlaylistsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MPPlaylistsFragment newInstance(String param1) {
        MPPlaylistsFragment fragment = new MPPlaylistsFragment();
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

        loadPlaylists();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mp_fragment_playlists, container, false);

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
        mfRecyclerViewAdapter = new CustomPlaylistsListAdapter(new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlTrackImageLayout) {
                showPlaylistTracks(pos);
            } else if (viewId == R.id.mlArtistLayout) {
                showPlaylistTracks(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                showPlaylistTracks(pos);
            }
        });
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadPlaylists();
    }

    private void showPlaylistTracks(int position) {
        if (position < 0) return;

        Playlist playlist = mfRecyclerViewAdapter.getItem(position);

        if (playlist != null && getActivity() != null) {
            ((MPMainActivity) getActivity()).gotoPlaylistActivity(playlist);
        }
    }

    private void showOptions(int position, View view) {
        if (position < 0) return;

        Playlist playlist = mfRecyclerViewAdapter.getItem(position);

        if (playlist == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.playlist_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_open) {
                showPlaylistTracks(position);
            } else if (item.getItemId() == R.id.action_play_all) {
                playAll(playlist);
            } else if (item.getItemId() == R.id.action_add_all) {
                addAll(playlist);
            } else if (item.getItemId() == R.id.action_delete_playlist) {
                deletePlaylist(position);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void playAll(Playlist playlist) {
        if (playlist == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).playPlaylistTracks(playlist);
        }
    }

    private void addAll(Playlist playlist) {
        if (playlist == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).addPlaylistTracksToQueue(playlist);
        }
    }

    private void deletePlaylist(int position) {
        if (position < 0) return;

        Playlist playlist = mfRecyclerViewAdapter.getItem(position);

        if (ContextUtils.isContextValid(this) && playlist != null && getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(getResources().getString(R.string.mp_delete_playlist));
            builder.setMessage(getString(R.string.mp_are_you_sure_delete_playlist, playlist.getName()));

            builder.setPositiveButton(getResources().getString(R.string.mp_yes), (dialog, which) -> deletePlaylistYes(playlist));

            builder.setNegativeButton(getResources().getString(R.string.mp_no), (dialog, which) -> {

                if (dialog != null && ContextUtils.isContextValid(this)) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void deletePlaylistYes(Playlist playlist) {
        if (getActivity() != null && ((MPBaseActivity) getActivity()).deletePlaylist(playlist)) {
            loadPlaylists();
        }
    }

    private void loadPlaylists() {
        AsyncTask.execute(this::loadPlaylistsAsyncTask);
    }

    private void loadPlaylistsAsyncTask() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mfRecyclerViewAdapter.clear());
        }

        List<Playlist> playlistList = SharedPrefUtils.getPlaylistArraySearchList(getContext(), query);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                if (playlistList != null && playlistList.size() > 0) {
                    mfRecyclerViewAdapter.setData(playlistList);
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
