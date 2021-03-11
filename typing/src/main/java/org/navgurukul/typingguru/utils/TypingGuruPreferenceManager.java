package org.navgurukul.typingguru.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class TypingGuruPreferenceManager {
    private SharedPreferences mSharedPreferences;
    private static final String PREF_NAME = "typing-guru";
    RemoteConfig remoteConfig;
    private TypingGuruPreferenceManager() {

    }

    private static class SingletonHolder {
        private static final TypingGuruPreferenceManager INSTANCE = new TypingGuruPreferenceManager();
    }

    public static final TypingGuruPreferenceManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public void init(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        if (remoteConfig == null) {
            remoteConfig = new RemoteConfig(FirebaseRemoteConfig.getInstance());
            remoteConfig.initialise();
        }
    }

    private SharedPreferences.Editor getEditor() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        return editor;
    }

    public void setWebViewDisplayStatus(boolean isShown) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean("isShown", isShown);
        editor.commit();
    }

    public boolean iWebViewShown() {
        return mSharedPreferences.getBoolean("isShown", false);
    }

    public RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }
}
