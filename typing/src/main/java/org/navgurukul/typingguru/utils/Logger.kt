package org.navgurukul.typingguru.utils

import android.util.Log

object Logger {
    private const val TAG = "TypingAssist"
    private const val DEBUG = true
    private const val VLOG = DEBUG && true
    private const val DLOG = DEBUG && true
    private const val ILOG = DEBUG && true
    private const val WLOG = DEBUG && true
    private const val ELOG = DEBUG && true
    fun v(tag: String, msg: String) {
        if (VLOG) {
            Log.v(TAG, "[$tag] $msg")
        }
    }

    fun d(tag: String, msg: String) {
        if (DLOG) {
            Log.d(TAG, "[$tag] $msg")
        }
    }

    fun i(tag: String, msg: String) {
        if (ILOG) {
            Log.i(TAG, "[$tag] $msg")
        }
    }

    fun w(tag: String, msg: String) {
        if (WLOG) {
            Log.w(TAG, "[$tag] $msg")
        }
    }

    fun e(tag: String, msg: String) {
        if (ELOG) {
            Log.e(TAG, "[$tag] $msg")
        }
    }
}