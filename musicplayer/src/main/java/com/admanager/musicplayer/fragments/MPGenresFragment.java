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
import com.admanager.musicplayer.adapters.CustomGenresListAdapter;
import com.admanager.musicplayer.models.Genre;
import com.admanager.musicplayer.tasks.GetTracks;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MPGenresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MPGenresFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String query;

    private OnFragmentInteractionListener mListener;

    private CustomGenresListAdapter mfRecyclerViewAdapter;

    public MPGenresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GenresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MPGenresFragment newInstance(String param1) {
        MPGenresFragment fragment = new MPGenresFragment();
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

        loadGenres();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mp_fragment_genres, container, false);

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
        mfRecyclerViewAdapter = new CustomGenresListAdapter(getContext(), new ArrayList<>(), (view2, pos) -> {
            long viewId = view2.getId();

            if (viewId == R.id.mlGenreImageLayout) {
                showGenreTracks(pos);
            } else if (viewId == R.id.mlGenreLayout) {
                showGenreTracks(pos);
            } else if (viewId == R.id.mlOptionLayout) {
                showOptions(pos, view2);
            } else {
                showGenreTracks(pos);
            }
        });
        faRecyclerViewList.setAdapter(mfRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadGenres();
    }

    private void loadGenres() {
        AsyncTask.execute(this::loadGenresAsyncTask);
    }

    private void loadGenresAsyncTask() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> mfRecyclerViewAdapter.clear());
        }

        List<Genre> genreList = GetTracks.getAllGenres(getContext(), query);

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                if (genreList != null && genreList.size() > 0) {
                    mfRecyclerViewAdapter.setData(genreList);
                }
            });
        }
    }

    private void showGenreTracks(int position) {
        if (position < 0) return;

        Genre genre = mfRecyclerViewAdapter.getItem(position);

        if (genre != null && getActivity() != null) {
            ((MPMainActivity) getActivity()).gotoGenreActivity(genre);
        }
    }

    private void showOptions(int position, View view) {
        if (position < 0) return;

        Genre genre = mfRecyclerViewAdapter.getItem(position);

        if (genre == null) return;

        //creating a popup menu
        PopupMenu popup = new PopupMenu(getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.album_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_open) {
                showGenreTracks(position);
            } else if (item.getItemId() == R.id.action_play_all) {
                playAll(genre);
            } else if (item.getItemId() == R.id.action_add_all) {
                addAll(genre);
            } else if (item.getItemId() == R.id.action_add_to_playlist) {
                addGenreTracksToPlaylist(genre);
            }

            return true;
        });
        //displaying the popup
        popup.show();
    }

    private void playAll(Genre genre) {
        if (genre == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).playGenreTracks(genre);
        }
    }

    private void addAll(Genre genre) {
        if (genre == null) return;

        if (getActivity() != null) {
            ((MPBaseMusicActivity) getActivity()).addGenreTracksToQueue(genre);
        }
    }

    private void addGenreTracksToPlaylist(Genre genre) {
        if (genre == null) return;

        if (getActivity() != null) {
            ((MPBaseActivity) getActivity()).addGenreTracksToPlaylist(genre);
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
