package com.admanager.unseen.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.admanager.unseen.R;
import com.admanager.unseen.adapters.MessagesAdapter;
import com.admanager.unseen.notiservice.NotiListenerService;
import com.admanager.unseen.notiservice.models.Conversation;
import com.admanager.unseen.notiservice.models.Message;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by a on 2.05.2017.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    EditText message_text;
    String conversation;
    String type;
    ImageView send;
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //toolbar
        conversation = getIntent().getStringExtra("conversation");
        setTitle(conversation);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        send = (ImageView) findViewById(R.id.send);
        send.setVisibility(View.INVISIBLE);

        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setUpRecyclerView();

        message_text = (EditText) findViewById(R.id.message_text);
        ImageView send2 = (ImageView) findViewById(R.id.send2);
        send2.setColorFilter(Color.WHITE);

    }

    private void setUpRecyclerView() {
        Conversation conversation = realm.where(Conversation.class).equalTo("title", this.conversation).findFirst();
        if (conversation == null) {
            finish();
            return;
        }
        RealmResults<Message> all = conversation.getMessages().sort("time");
        adapter = new MessagesAdapter(all, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        recyclerView.setHasFixedSize(true);
        //        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        type = conversation.getType();
        try {
            Resources resources = getResources();
            final int resourceId = resources.getIdentifier(type, "drawable", getPackageName());
            this.send.setImageDrawable(ContextCompat.getDrawable(this, resourceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        send.setVisibility(View.VISIBLE);
        send.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        realm.close();
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemId = menuitem.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.action_scroll_bottom) {
            int c = adapter.getItemCount() - 1;
            recyclerView.scrollToPosition(c);
                /* case R.id.action_delete:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
                finish();
                break;*/
        } else {
            return super.onOptionsItemSelected(menuitem);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (this.type == null) {
            return;
        }

        ArrayList<String> packs = NotiListenerService.getPackageFromType(this.type);

        try {
            send(findInstalledPackage(packs));
            message_text.setText("");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String findInstalledPackage(ArrayList<String> packs) {
        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;

            for (String pack : packs) {
                if (pack.equals(packageName)) {
                    return packageName;
                }
            }
        }
        return null;
    }

    private void send(String pack) {
        String s = message_text.getText().toString();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setPackage(pack);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(Intent.createChooser(i, getString(R.string.share_app_chooser)));
    }
}
