package com.admanager.applocker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.admanager.applocker.AppLockerApp;
import com.admanager.applocker.R;
import com.admanager.applocker.prefrence.Prefs;
import com.admanager.core.Ads;

import java.util.ArrayList;
import java.util.List;

public class PasswordRecoveryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String CHECK = "check";
    Spinner questionsSpinner;
    EditText answer;
    TextView content_recovery;
    Button confirmButton;
    int questionNumber = 0;
    private boolean check;
    private Ads ads;

    public static void startActivity(Context context, boolean check) {
        Intent intent = new Intent(context, PasswordRecoveryActivity.class);
        intent.putExtra(CHECK, check);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_password);

        ads = AppLockerApp.getInstance().getAds();
        ads.loadBottom(this, (LinearLayout) findViewById(R.id.bottom_container));
        ads.loadTop(this, (LinearLayout) findViewById(R.id.top_container));

        check = getIntent().getBooleanExtra(CHECK, false);

        confirmButton = findViewById(R.id.confirmButton);
        questionsSpinner = findViewById(R.id.questionsSpinner);
        answer = findViewById(R.id.answer);
        content_recovery = findViewById(R.id.content_recovery);

        content_recovery.setText(check ? R.string.recovery_text_recover : R.string.recovery_text_init);
        confirmButton.setText(check ? R.string.recovery_button_recover : R.string.recovery_button_init);

        List<String> list = new ArrayList<>();
        list.add("Select your security question?");
        list.add("What is your pet name?");
        list.add("Who is your favorite teacher?");
        list.add("Who is your favorite actor?");
        list.add("Who is your favorite actress?");
        list.add("Who is your favorite cricketer?");
        list.add("Who is your favorite footballer?");

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionsSpinner.setAdapter(stringArrayAdapter);

        questionsSpinner.setOnItemSelectedListener(this);

        confirmButton.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if (!check) {
            // reset password and ask it again
            Prefs.with(this).passwordSet(false);
            Prefs.with(this).savePassword("");
            PasswordActivity.startPasswordSet(this, true);
        }
        finish();
        //        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        questionNumber = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (questionNumber == 0 || TextUtils.isEmpty(answer.getText())) {
            Toast.makeText(getApplicationContext(), "Please select a question and write an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        Prefs with = Prefs.with(this);
        if (!check) {
            with.passwordSet(true);
            with.setRememberAnswer(answer.getText().toString());
            with.setQuestionNumber(questionNumber);
            finish();
            return;
        }

        boolean same = with.getQuestionNumber() == questionNumber && with.getRememberAnswer().matches(answer.getText().toString());
        if (!same) {
            Toast.makeText(getApplicationContext(), "Your question and answer didn't matches", Toast.LENGTH_SHORT).show();
            return;
        }

        with.passwordSet(false);
        with.savePassword("");
        PasswordActivity.startPasswordSet(this, false);
        finish();

    }
}
