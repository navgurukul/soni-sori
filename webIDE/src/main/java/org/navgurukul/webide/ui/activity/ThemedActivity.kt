package org.navgurukul.webide.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat
import org.navgurukul.webide.util.ui.Styles

abstract class ThemedActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (shouldInstallDynamicModule()) {
            SplitCompat.installActivity(this)
        }
    }

    open fun shouldInstallDynamicModule() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Styles.getThemeInt(this))
        super.onCreate(savedInstanceState)
    }
}