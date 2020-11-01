package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.SearchView
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.util.AppUtils
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.themes.getThemedColor
import timber.log.Timber

@Parcelize
data class MainActivityArgs(
    val clearNotification: Boolean
) : Parcelable

class MainActivity : AppCompatActivity(), ToolbarConfigurable {

    companion object {
        const val KEY_ARG = "MainActivity:args"
        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }

        fun newIntent(context: Context, clearNotification: Boolean = false): Intent {
            val args = MainActivityArgs(
                clearNotification = clearNotification
            )

            return Intent(context, MainActivity::class.java)
                .apply {
                    putExtra(KEY_ARG, args)
                }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getParcelableExtra<MainActivityArgs>(KEY_ARG)?.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }
    }


    private val appOpenDelegate: AppOpenDelegate by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        nav_view.setupWithNavController(navHostFragment.navController)

        intent.getParcelableExtra<MainActivityArgs>(KEY_ARG)?.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }
        findViewById<View>(R.id.headerIv).setOnClickListener {
            if (AppUtils.isFakeLogin(this))
                OnBoardingActivity.launchLoginFragment(this)
            else
                ProfileActivity.launch(this)
        }

        fetchRemoteConfigAndUpdate()
    }

    private fun fetchRemoteConfigAndUpdate() {
        val remoteConfig = Firebase.remoteConfig
        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        var data = remoteConfig.getValue("rc_available_lang").asString()
        AppUtils.saveLanguageConfig(data, this)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    data = remoteConfig.getValue("rc_available_lang").asString()
                    Timber.d("Config params updated: $updated  with values $data")
                    AppUtils.saveLanguageConfig(data, this)
                } else {
                    Timber.w("fetchRemoteConfigAndUpdate Failed")
                }
            }
    }

    override fun configure(toolbar: Toolbar) {
        throw RuntimeException("Custom Toolbar Not supported")
    }

    override fun setTitle(title: String, @AttrRes colorRes: Int) {
        headerTitle.text = title
        headerTitle.setTextColor(getThemedColor(colorRes))
    }
}