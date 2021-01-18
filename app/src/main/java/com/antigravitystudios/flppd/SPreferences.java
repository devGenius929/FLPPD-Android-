package com.antigravitystudios.flppd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.antigravitystudios.flppd.models.User;
import com.google.gson.Gson;

public class SPreferences {

    private final static String USER = "USER";
    private final static String JUST_LOGGED = "JUST_LOGGED";
    private final static String TOKEN = "TOKEN";
    private final static String PUSH_TOKEN = "PUSH_TOKEN";
    private final static String REMEMBER_USER = "REMEMBER_USER";
    private final static String TUTORIAL_SHOWN = "TUTORIAL_SHOWN";
    private final static String MESSAGES_LAST_SYNC = "MESSAGES_LAST_SYNC";

    private static User user;

    private static Gson gson = new Gson();
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void clearSession() {
        sharedPreferences.edit()
                .remove(USER)
                .remove(TOKEN)
                .remove(REMEMBER_USER)
                .remove(PUSH_TOKEN)
                .remove(JUST_LOGGED)
                .remove(MESSAGES_LAST_SYNC)
                .apply();
        user = null;
    }

    public static void saveSession(String token, User user, boolean remember) {
        sharedPreferences.edit()
                .putString(TOKEN, token)
                .putString(USER, gson.toJson(user))
                .putBoolean(REMEMBER_USER, remember)
                .apply();
    }

    public static void setTutorialShown() {
        sharedPreferences.edit()
                .putBoolean(TUTORIAL_SHOWN, true)
                .apply();
    }

    public static boolean isTutorialShown() {
        return sharedPreferences.getBoolean(TUTORIAL_SHOWN, false);
    }

    public static boolean isLogged() {
        return sharedPreferences.contains(TOKEN) && sharedPreferences.getBoolean(REMEMBER_USER, false);
    }

    public static User getUser() {
        if (user == null && sharedPreferences.contains(USER)) {
            user = gson.fromJson(sharedPreferences.getString(USER, null), User.class);
        }
        return user;
    }

    public static void setUser(User user) {
        SPreferences.user = user;
        sharedPreferences.edit()
                .putString(USER, gson.toJson(user))
                .apply();
        user.notifyChange();
    }

    public static String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public static long getMessagesLastSync() {
        return sharedPreferences.getLong(MESSAGES_LAST_SYNC, 0);
    }

    public static void setMessagesLastSync(long time) {
        sharedPreferences.edit()
                .putLong(MESSAGES_LAST_SYNC, time)
                .apply();
    }

    public static void setPushToken(String token) {
        sharedPreferences.edit()
                .putString(PUSH_TOKEN, token)
                .apply();
    }

    public static String getPushToken() {
        return sharedPreferences.getString(PUSH_TOKEN, "");
    }

    public static boolean isJustLogged() {
        return sharedPreferences.getBoolean(JUST_LOGGED, true);
    }

    public static void setJustLogged() {
        sharedPreferences.edit()
                .putBoolean(JUST_LOGGED, false)
                .apply();
    }
}