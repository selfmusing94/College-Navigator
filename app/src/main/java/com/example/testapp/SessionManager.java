package com.example.testapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "User Session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Create login session
    public void createLoginSession(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Get stored email
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Clear session
    public void logoutUser () {
        editor.clear();
        editor.apply();
    }
}