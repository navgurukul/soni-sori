package org.merakilearn

import android.app.AlertDialog
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.core.extentions.activityArgs
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.LoginResponse
import org.merakilearn.ui.onboarding.OnBoardingActivity
import org.merakilearn.ui.profile.ProfileActivity
import org.merakilearn.ui.profile.ProfileFragment
import org.merakilearn.ui.profile.ProfileViewActions
import org.merakilearn.ui.profile.ProfileViewModel
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.themes.getThemedColor
import org.navgurukul.learn.courses.db.models.Pathway
import org.navgurukul.learn.courses.repository.LearnRepo

@Parcelize
data class MainActivityArgs(
    val clearNotification: Boolean = false,
    val selectedPathwayId: Int? = null
) : Parcelable

class MainActivity : AppCompatActivity(), ToolbarConfigurable {

    private lateinit var firebaseAnalytics : FirebaseAnalytics
    companion object {
        fun launch(context: Context, selectedPathwayId: Int? = null) {
            val intent = newIntent(context, selectedPathwayId = selectedPathwayId)
            context.startActivity(intent)
        }

        fun newIntent(
            context: Context,
            clearNotification: Boolean = false,
            selectedPathwayId: Int? = null
        ): Intent {
            val args = MainActivityArgs(
                clearNotification = clearNotification,
                selectedPathwayId = selectedPathwayId
            )

            return Intent(context, MainActivity::class.java)
                .apply {
                    putExtras(args.toBundle()!!)
                }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        mainActivityArgs.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }
    }


    private val appOpenDelegate: AppOpenDelegate by inject()
    private val mainActivityArgs: MainActivityArgs by activityArgs()
    private val userRepo: UserRepo by inject()
    private val learnRepo: LearnRepo by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAnalytics= Firebase.analytics
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        nav_view.setupWithNavController(navHostFragment.navController)

        if (mainActivityArgs.selectedPathwayId != null) {
            navHostFragment.navController.navigate(R.id.navigation_learn)
        }

        mainActivityArgs.let { args ->
            appOpenDelegate.onHomeScreenOpened(this, args.clearNotification)
        }

        findViewById<ImageView>(R.id.headerIv).let {
            userRepo.getCurrentUser()?.let { currentUser ->
                setUserThumbnail(it, currentUser)
            } ?: run {
                OnBoardingActivity.restartApp(this@MainActivity)
            }
        }

        findViewById<ImageView>(R.id.headerLogOut).let {
            setUserLogoutThumbnail(it)
        }

    }

    private fun setUserLogoutThumbnail(
        it: ImageView
    ){
        val requestOptions = RequestOptions()
            .centerCrop()
            .transform(CircleCrop())

        val thumbnail = GlideApp.with(this)
            .load(R.drawable.ic_log_out)
            .apply(requestOptions)

        GlideApp.with(it)
            .load(R.drawable.ic_log_out)
            .apply(requestOptions)
            .thumbnail(thumbnail)
            .transform(CircleCrop())
            .into(it)

    }

    private fun setUserThumbnail(
        it: ImageView,
        currentUser: LoginResponse.User
    ) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .transform(CircleCrop())

        firebaseAnalytics.setUserId(currentUser.id)

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
            if (userRepo.isFakeLogin())
                OnBoardingActivity.showLoginScreen(this)
            else
                ProfileActivity.launch(this)
        }
    }


    override fun configure(toolbar: Toolbar) {
        throw RuntimeException("Custom Toolbar Not supported")
    }

    override fun configure(
        title: String,
        @AttrRes colorRes: Int,
        showProfile: Boolean,
        subtitle: String?,
        onClickListener: View.OnClickListener?,
        action: String?,
        actionOnClickListener: View.OnClickListener?,
        showLogout: Boolean,
        showPathwayIcon : Boolean,
        pathwayIcon: String?
    ) {
        headerTitle.text = title
        headerTitle.setTextColor(getThemedColor(colorRes))

        subtitle?.let {
            headerSubtitle.text = subtitle
            headerSubtitle.isVisible = true
        } ?: run {
            headerSubtitle.isVisible = false
        }

        action?.let {
            headerAction.text = action
            headerAction.isVisible = true
            actionOnClickListener?.let { listener ->
                headerActionClickArea.setOnClickListener {
                    listener.onClick(it)
                }
            }
        } ?: run {
            headerAction.isVisible = false
        }

        headerIv.isVisible = showProfile
        headerLogOut.isVisible = showLogout

        headerIcon.isVisible = showPathwayIcon
        pathwayIcon?.let {
            GlideApp.with(headerIcon)
                .load(it)
                .transform(CircleCrop())
                .into(headerIcon)
        }

        onClickListener?.let { listener ->
            appToolbar.setOnClickListener {
                listener.onClick(it)
            }
        }
    }
}