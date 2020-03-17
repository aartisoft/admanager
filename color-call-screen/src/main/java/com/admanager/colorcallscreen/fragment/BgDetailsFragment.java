package com.admanager.colorcallscreen.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.utils.Prefs;
import com.admanager.colorcallscreen.utils.Utils;
import com.bumptech.glide.Glide;

import java.io.File;

public class BgDetailsFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "CategoryFragment";
    View back;
    Button setAsCall;
    Button listContacts;
    Button addContact;
    ImageView selected;
    ImageView ivPortrait;
    ImageView image;
    TextView textDisplayName;
    TextView textNumber;
    BgModel bg;
    boolean fileSaved;

    public static Bundle createBgDetailsBundle(String name, Uri uri, BgModel bgModel, String number, boolean fileSaved) {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("number", number);
        bundle.putParcelable("uri", uri);
        bundle.putSerializable("bg", bgModel);
        bundle.putBoolean("file_saved", fileSaved);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ccs_fragment_bg_details, parent, false);

        back = view.findViewById(R.id.back);
        setAsCall = view.findViewById(R.id.set_as_call);
        listContacts = view.findViewById(R.id.list_contacts);
        addContact = view.findViewById(R.id.add_contact);
        textDisplayName = view.findViewById(R.id.text_display_name);
        textNumber = view.findViewById(R.id.text_number);
        ivPortrait = view.findViewById(R.id.iv_portrait);
        image = view.findViewById(R.id.image);
        selected = view.findViewById(R.id.selected);

        back.setOnClickListener(this);
        setAsCall.setOnClickListener(this);
        listContacts.setOnClickListener(this);
        addContact.setOnClickListener(this);

        Bundle args = getArguments();

        bg = (BgModel) args.getSerializable("bg");
        fileSaved = args.getBoolean("file_saved", false);

        updateSelected();

        textDisplayName.setText(args.getString("name"));
        textNumber.setText(args.getString("number"));

        Glide.with(getContext())
                .load((Uri) args.getParcelable("uri"))
                .into(ivPortrait);

        Glide.with(getContext())
                .load(bg.image)
                .into(image);

        return view;
    }

    private void updateSelected() {
        boolean isSelected = Prefs.with(getContext()).isSelectedBg(bg);
        boolean isSelectedForUser = Prefs.with(getContext()).isSelectedBgForUser(null, bg);
        selected.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        setAsCall.setText(isSelected ? R.string.call_screen_cancel : R.string.call_screen_set);
        setAsCall.setBackgroundResource(isSelected ? R.drawable.setas_disabled : R.drawable.btn_setas);

        listContacts.setText(isSelectedForUser ? R.string.contact_list : R.string.contact_select);
        addContact.setVisibility(isSelectedForUser ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getColorCallScreenActivity().getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        getColorCallScreenActivity().getSupportActionBar().show();
    }

    private void setAsBg(final ContactBean contactBean) {
        getColorCallScreenActivity().askPermissions(new Runnable() {
            @Override
            public void run() {
                if (!fileSaved) {
                    Utils.downloadBgImage(getColorCallScreenActivity(), bg.image, new Utils.DownloadListener() {
                        @Override
                        public void downloaded(File file) {
                            setAsBg(getColorCallScreenActivity(), file, bg, contactBean);
                        }
                    });
                } else {
                    setAsBg(getColorCallScreenActivity(), null, bg, contactBean);
                }
            }
        });

    }

    private void setAsBg(final Activity context, File file, BgModel bg, @Nullable ContactBean contactBean) {
        if (file == null) {
            Log.e(TAG, "no file for bg: " + bg.toString());
        }
        if (file != null && !fileSaved) {
            File fileDir = Utils.getFilesFolder(context, bg);
            Utils.copyFile(file, fileDir);
        }

        Prefs.with(getContext()).addSelectedBgForUser(contactBean, bg);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateSelected();
                Toast.makeText(context, getString(R.string.ccs_changed), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            getColorCallScreenActivity().onBackPressed();
        } else if (id == R.id.set_as_call) {
            boolean isSelected = Prefs.with(getContext()).isSelectedBg(bg);
            if (!isSelected) {
                setAsBg(null);
            } else {
                Prefs.with(getColorCallScreenActivity()).resetSelectedBg();
                updateSelected();
            }
        } else if (id == R.id.list_contacts) {
            boolean isSelectedForUser = Prefs.with(getContext()).isSelectedBgForUser(null, bg);
            if (isSelectedForUser) {
                ContactListFragment instance = ContactListFragment.instance(bg);
                FragmentManager fm = getFragmentManager();
                instance.show(fm, "dialog");
                fm.executePendingTransactions();
                fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                        super.onFragmentViewDestroyed(fm, f);
                        if (f instanceof ContactListFragment) {
                            updateSelected();
                        }
                        fm.unregisterFragmentLifecycleCallbacks(this);
                    }
                }, false);
//                    instance.getDialog().setOnDismissListener(dialog -> updateSelected());
            } else {
                getColorCallScreenActivity().askForContactPick(new ContactSelectedListener() {
                    @Override
                    public void selected(ContactBean contact) {
                        setAsBg(contact);
                    }
                });
            }
        } else if (id == R.id.add_contact) {
            getColorCallScreenActivity().askForContactPick(new ContactSelectedListener() {
                @Override
                public void selected(ContactBean contact) {
                    setAsBg(contact);
                }
            });
        }
    }

    public interface ContactSelectedListener {
        void selected(ContactBean contact);
    }
}