package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.util.AppUtils
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.themes.getThemedColor

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

        findViewById<ImageView>(R.id.headerIv).let {
            val currentUser = AppUtils.getCurrentUser(this)

            GlideApp.with(it)
                .load(currentUser.profilePicture)
                .placeholder(R.drawable.illus_default_avatar)
                .fallback(R.drawable.illus_default_avatar)
                .transform(CircleCrop())
                .into(it)

            it.setOnClickListener {
                if (AppUtils.isFakeLogin(this))
                    OnBoardingActivity.launchLoginFragment(this)
                else
                    ProfileActivity.launch(this)
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