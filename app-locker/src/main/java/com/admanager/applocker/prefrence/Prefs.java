package com.admanager.applocker.prefrence;

import android.content.Context;
import android.content.SharedPreferences;

import com.admanager.applocker.utils.PasswordType;

import java.util.HashSet;
import java.util.Set;

public class Prefs {
    private static final String LOCKED_APP = "locked_app_set";
    private static final String MyPREFERENCES = "MyPreferences";
    private static final String IS_PASSWORD_SET = "is_password_set";
    private static final String PASSWORD = "password";
    private static final String ANSWER = "answer";
    private static final String QUESTION_NUMBER = "question_number";
    private static final String PASSWORD_TYPE = "password_type";

    private static Prefs instance;
    private final SharedPreferences sharedPreferences;

    Prefs(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static Prefs with(Context context) {
        if (instance == null) {
            instance = new Prefs(context);
        }
        return instance;
    }

    // This four methods are used for maintaining favorites.
    private void saveLocked(Set<String> lockedApp) {
        sharedPreferences.edit()
                .putStringSet(LOCKED_APP, lockedApp)
                .apply();
    }

    public void addLocked(String app) {
        Set<String> lockedApp = getLocked(true);
        if (lockedApp == null) {
            lockedApp = new HashSet<>();
        }
        lockedApp.add(app);
        saveLocked(lockedApp);
    }

    public void removeLocked(String app) {
        Set<String> locked = getLocked(true);
        if (locked != null) {
            locked.remove(app);
            saveLocked(locked);
        }
    }

    public Set<String> getLocked() {
        return getLocked(false);
    }

    private Set<String> getLocked(boolean newObject) {
        Set<String> old = sharedPreferences.getStringSet(LOCKED_APP, null);
        if (!newObject) {
            return old;
        }
        if (old == null) {
            return null;
        }
        return new HashSet<>(old);
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, "");
    }

    public void savePassword(String password) {
        sharedPreferences.edit()
                .putString(PASSWORD, password)
                .apply();
    }

    public boolean isPasswordSet() {
        return sharedPreferences.getBoolean(IS_PASSWORD_SET, false);
    }

    public void passwordSet(boolean set) {
        sharedPreferences.edit()
                .putBoolean(IS_PASSWORD_SET, set)
                .apply();
    }

    public String getRememberAnswer() {
        return sharedPreferences
                .getString(ANSWER, "");
    }

    public void setRememberAnswer(String answer) {
        sharedPreferences.edit()
                .putString(ANSWER, answer)
                .apply();
    }

    public int getQuestionNumber() {
        return sharedPreferences
                .getInt(QUESTION_NUMBER, 0);
    }

    public void setQuestionNumber(int questionNumber) {
        sharedPreferences.edit()
                .putInt(QUESTION_NUMBER, questionNumber)
                .apply();
    }

    public PasswordType getPasswordType() {
        String type = sharedPreferences
                .getString(PASSWORD_TYPE, PasswordType.PATTERN.name());
        return PasswordType.valueOf(type);
    }

    public void setPasswordType(PasswordType type) {
        sharedPreferences.edit()
                .putString(PASSWORD_TYPE, type.name())
                .apply();
    }
}
