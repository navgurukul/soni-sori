package org.merakilearn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.merakilearn.core.appopen.AppOpenDelegate
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import org.merakilearn.databinding.ActivityOnBoardingBinding
import org.merakilearn.datasource.UserRepo
import org.merakilearn.ui.onboarding.LoginFragment
import org.merakilearn.ui.onboarding.WelcomeFragment

@Parcelize
data class OnBoardingActivityArgs(
    val clearNotification: Boolean
) : Parcelable

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityOnBoardingBinding

    private val appOpenDelegate: AppOpenDelegate by inject()
    private val userRepo: UserRepo by inject()

    companion object {

        const val KEY_ARG = "OnBoardingActivity:args"

        fun newIntent(context: Context, clearNotification: Boolean = false): Intent {
            val args = OnBoardingActivityArgs(
                clearNotification = clearNotification
            )

            return Intent(context, OnBoardingActivity::class.java)
                .apply {
                    putExtra(KEY_ARG, args)
                }
        }

        // Special action to clear cache and/or clear credentials
        fun restartApp(activity: Activity, args: OnBoardingActivityArgs) {
            val intent = Intent(activity, OnBoardingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            intent.putExtra(KEY_ARG, args)
            activity.startActivity(intent)
        }

        fun launchLoginFragment(context: FragmentActivity) {
            val intent = Intent(context, OnBoardingActivity::class.java)
            intent.putExtra(LAUNCH_LOGIN, true)
            context.startActivity(intent)
        }

        private const val LAUNCH_LOGIN = "arg_launch_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        if (intent.hasExtra(LAUNCH_LOGIN) && intent.getBooleanExtra(LAUNCH_LOGIN, false)) {
            showFragment(LoginFragment.newInstance(), LoginFragment.TAG)
        } else {
            startDestinationActivity()
        }

        intent.getParcelableExtra<OnBoardingActivityArgs>(KEY_ARG)?.let { args ->
            appOpenDelegate.onAppOpened(this, args.clearNotification)
        }
    }

    private fun startDestinationActivity() {
        if (userRepo.isUserLoggedIn())
            MainActivity.launch(this)
        else
            showFragment(WelcomeFragment.newInstance(), WelcomeFragment.TAG)
    }

    private fun showFragment(fragment: Fragment, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container_onboarding, fragment, tag)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commitAllowingStateLoss()
    }
}