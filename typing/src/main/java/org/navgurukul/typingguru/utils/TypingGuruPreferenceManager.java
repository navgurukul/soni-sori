package org.navgurukul.typingguru.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TypingGuruPreferenceManager {
    private SharedPreferences mSharedPreferences;
    private static final String PREF_NAME = "typing-guru";
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
}
