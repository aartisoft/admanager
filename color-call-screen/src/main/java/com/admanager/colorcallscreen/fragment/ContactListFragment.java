package com.admanager.colorcallscreen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.adapter.ContactListAdapter;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class ContactListFragment extends DialogFragment {
    public static final String TAG = "ContactListFragment";

    ArrayList<ContactBean> contactIdList;
    ContactListAdapter adapter;
    RecyclerView recyclerView;
    View button;

    public static ContactListFragment instance(BgModel bg) {
        ContactListFragment frag = new ContactListFragment();
        Bundle args = new Bundle();
        args.putSerializable("bg", bg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ccs_fragment_contact_list, parent, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        button = view.findViewById(R.id.button);

        BgModel bg = (BgModel) getArguments().getSerializable("bg");
        List<String> list = Prefs.with(getContext()).getContactIdListForBg(bg);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contactIdList = new ArrayList<>();
        for (String s : list) {
            ContactBean contactBean = com.admanager.colorcallscreen.service.Utils.findContactById(getContext(), s);
            contactIdList.add(contactBean);
        }

        adapter = new ContactListAdapter(getActivity());
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        adapter.setData(contactIdList);

        return view;
    }
}