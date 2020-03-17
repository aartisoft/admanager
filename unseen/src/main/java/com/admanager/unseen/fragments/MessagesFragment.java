package com.admanager.unseen.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.unseen.R;
import com.admanager.unseen.adapters.ConversationsAdapter;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.utils.Utils;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MessagesFragment extends BaseFragment {

    private static final String TAG = "MessagesFragment";
    private ConversationsAdapter adapter;

    public MessagesFragment() {
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    protected ConversationsAdapter getAdapter(String tag) {
        RealmQuery<Conversation> q = realm.where(Conversation.class);
        if (!TextUtils.isEmpty(tag)) {
            q = q.equalTo("type", tag);
        }
        RealmResults<Conversation> all = q.findAll().sort("lastMessage.time", Sort.DESCENDING);
        adapter = new ConversationsAdapter((AppCompatActivity) getActivity(), all, true);
        return adapter;
    }

    @Override
    protected boolean showRecyclerView() {
        return Utils.checkNotificationEnabled(getContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.delete_dialog_title))
                    .setMessage(getString(R.string.delete_dialog_message))
                    .setNeutralButton(getString(R.string.delete_dialog_no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .setPositiveButton(getString(R.string.delete_dialog_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recyclerView.setAdapter(null);
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.deleteAll();
                                }
                            });
                            RealmResults<Conversation> all = realm.where(Conversation.class).findAll().sort("lastMessage.time", Sort.DESCENDING);
                            adapter.updateData(all);
                            recyclerView.setAdapter(adapter);
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
