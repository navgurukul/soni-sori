package org.navgurukul.webide.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.navgurukul.webide.util.ui.Styles

abstract class ThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Styles.getThemeInt(this))
        super.onCreate(savedInstanceState)
    }
}