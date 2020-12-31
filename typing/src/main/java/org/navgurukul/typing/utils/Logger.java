package org.navgurukul.typing.utils;

import android.util.Log;

public class Logger {
    private static final String TAG = "TypingAssist";
    private static final boolean DEBUG = true;
    private static final boolean VLOG = DEBUG && true;
    private static final boolean DLOG = DEBUG && true;
    private static final boolean ILOG = DEBUG && true;
    private static final boolean WLOG = DEBUG && true;
    private static final boolean ELOG = DEBUG && true;

    private Logger() {
    }

    public static void v(String tag, String msg) {
        if (VLOG) {
            Log.v(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DLOG) {
            Log.d(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (ILOG) {
            Log.i(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (WLOG) {
            Log.w(TAG, "[" + tag + "] " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (ELOG) {
            Log.e(TAG, "[" + tag + "] " + msg);
        }
    }
}
