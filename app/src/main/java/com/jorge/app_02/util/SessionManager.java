package com.jorge.app_02.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    //Salva o nome de usuário e o ID do usuário logado.
    public void saveLoginSession(String username, int userId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.putInt(KEY_LOGGED_IN_USER_ID, userId);
        editor.apply();
    }

    //Obtém o ID do usuário logado.
    public int getLoggedInUserId() {
        return prefs.getInt(KEY_LOGGED_IN_USER_ID, -1);
    }

    //Obtém o nome de usuário logado.
    public String getLoggedInUsername() {
        return prefs.getString(KEY_LOGGED_IN_USERNAME, null);
    }

    //Limpa a sessão do usuário.
    public void clearLoginSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_LOGGED_IN_USERNAME);
        editor.remove(KEY_LOGGED_IN_USER_ID);
        editor.apply();
    }
}