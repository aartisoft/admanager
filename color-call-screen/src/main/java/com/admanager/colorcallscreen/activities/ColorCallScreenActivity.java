package com.admanager.colorcallscreen.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.admanager.colorcallscreen.ColorCallScreenApp;
import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.fragment.BgDetailsFragment;
import com.admanager.colorcallscreen.fragment.CategoryFragment;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.service.PhoneStateService;
import com.admanager.colorcallscreen.utils.Prefs;

public class ColorCallScreenActivity extends AppCompatActivity {
    public static final int REQUEST_PICK_CONTACT = 999;
    public static final int REQUEST_GET_SINGLE_FILE = 997;
    private static final String TAG = "ColorCallScreenActivity";
    private static final int CONTACT_PERMISSION = 998;
    private final int CODE_DEFAULT_DIALER = 123;
    public NavController navController;
    MenuItem flashMenuItem;
    private Runnable runAfterPermission;
    private BgDetailsFragment.ContactSelectedListener contactSelectedListener;

    public static void start(Context context) {
        Intent intent = new Intent(context, ColorCallScreenActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, BgModel bgModel, int position) {
        Intent intent = new Intent(context, ColorCallScreenActivity.class);
        intent.putExtra("BgModel", bgModel);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_call_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ColorCallScreenApp instance = ColorCallScreenApp.getInstance();
        if (instance != null) {
            if (instance.ads != null) {
                instance.ads.loadTop(this, (LinearLayout) findViewById(R.id.top));
                instance.ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom));
            }
            if (instance.title != null) {
                setTitle(instance.title);
            }

            if (instance.bgColor != 0) {
                findViewById(R.id.root).setBackgroundColor(ContextCompat.getColor(this, instance.bgColor));
            }

// todo style

        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.categoryFragment);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PhoneStateService.start(this);
        }

        if (getIntent() != null) {
            BgModel bgModel = (BgModel) getIntent().getSerializableExtra("BgModel");
            int position = getIntent().getIntExtra("position", -1);
            if (position != -1 && bgModel != null) {
                String name = ColorCallScreenApp.NAMES[position % ColorCallScreenApp.NAMES.length];
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + (ColorCallScreenApp.IMAGES[position % ColorCallScreenApp.IMAGES.length]));
                Bundle bundle = BgDetailsFragment.createBgDetailsBundle(name, uri, bgModel, ColorCallScreenApp.NUMBER, false);
                navController.navigate(R.id.bgDetailsFragment, bundle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ccs_options, menu);

        flashMenuItem = menu.findItem(R.id.nav_flash);
        flashMenuItem.setChecked(Prefs.with(this).isFlashEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_flash) {
            flashMenuItem.setChecked(!flashMenuItem.isChecked());
            Prefs.with(this).setFlashEnabled(flashMenuItem.isChecked());
            return true;
        } else {
            NavDestination cd = navController.getCurrentDestination();
            if (cd != null && cd.getId() == R.id.categoryFragment && item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
    }

    public void askForContactPick(BgDetailsFragment.ContactSelectedListener contactSelectedListener) {
        boolean afterM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        this.contactSelectedListener = contactSelectedListener;
        if (!afterM || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, ColorCallScreenActivity.REQUEST_PICK_CONTACT);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION);
        }

    }

    public void askPermissions(Runnable runnable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // set default dialer
            TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (!getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
                runAfterPermission = runnable;
                Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                        .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, CODE_DEFAULT_DIALER);
            } else {
                runnable.run();
            }
        } else {
            // its just for PRE M devices
            PhoneStateService.start(ColorCallScreenActivity.this);
            runnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CONTACT_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "PERMISSION GRANTED");

                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, ColorCallScreenActivity.REQUEST_PICK_CONTACT);

                } else {
                    // no permissions granted.
                    Log.i(TAG, "PERMISSION IS NOT GRANTED");
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DEFAULT_DIALER && resultCode == RESULT_OK) {
            if (runAfterPermission != null) {
                runAfterPermission.run();
                runAfterPermission = null;
            }
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_GET_SINGLE_FILE) {
            Uri selectedImageUri = data.getData();

            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (fragment instanceof CategoryFragment) {
                    ((CategoryFragment) fragment).imagePicked(selectedImageUri);
                }
            }

        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_CONTACT) {
            Uri selectedContactUri = data.getData();
            if (selectedContactUri == null) {
                return;
            }
            Cursor cursor = getContentResolver().query(selectedContactUri, null, null, null, null);

            if (cursor == null) {
                return;
            }

            if (cursor.moveToFirst()) {
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                contactSelectedListener.selected(new ContactBean(id, name, phoneNo, uri));
            }
            cursor.close();

        }
    }

}