package org.navgurukul.commonui.platform

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat

abstract class BaseActivity: AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (shouldInstallDynamicModule()) {
            SplitCompat.installActivity(this)
        }
    }

    open fun shouldInstallDynamicModule() = false
}