package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.profile.ProfileActivity
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
            AppUtils.getCurrentUser(this)?.let { currentUser ->
                setUserThumbnail(it, currentUser)
            } ?: run {
                OnBoardingActivity.restartApp(this@MainActivity, OnBoardingActivityArgs(true))
            }
        }
    }

    private fun setUserThumbnail(
        it: ImageView,
        currentUser: LoginResponse.User
    ) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .transform(CircleCrop())

        val thumbnail = GlideApp.with(this)
            .load(R.drawable.illus_default_avatar)
            .apply(requestOptions)

        GlideApp.with(it)
            .load(currentUser.profilePicture)
            .apply(requestOptions)
            .thumbnail(thumbnail)
            .transform(CircleCrop())
            .into(it)

        it.setOnClickListener {
            if (AppUtils.isFakeLogin(this))
                OnBoardingActivity.launchLoginFragment(this)
            else
                ProfileActivity.launch(this)
        }
    }

    override fun configure(toolbar: Toolbar) {
        throw RuntimeException("Custom Toolbar Not supported")
    }

    override fun configure(
        title: String,
        subtitle: String?,
        @AttrRes colorRes: Int,
        showProfile: Boolean,
        onClickListener: View.OnClickListener?
    ) {
        headerTitle.text = title
        headerTitle.setTextColor(getThemedColor(colorRes))

        subtitle?.let {
            headerSubtitle.text = subtitle
            headerSubtitle.isVisible = true
        } ?: run {
            headerSubtitle.isVisible = false
        }

        headerIv.isVisible = showProfile

        onClickListener?.let { listener ->
            appToolbar.setOnClickListener {
                listener.onClick(it)
            }
        }

    }
}