// AuthRepository.java
package com.dkaverznev.carcamera.data;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthRepository {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private final SharedPreferences sharedPreferences;

    public AuthRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public String getStoredEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getStoredPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public boolean isUserRegistered() {
        return sharedPreferences.contains(KEY_EMAIL);
    }
}
