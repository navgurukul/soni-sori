package org.merakilearn.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.MainActivity
import org.merakilearn.R
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.databinding.ActivityOnBoardingBinding

@Parcelize
data class OnBoardingActivityArgs(
    val clearNotification: Boolean,
    val showLoginFragment: Boolean = false
) : Parcelable

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityOnBoardingBinding

    private val appOpenDelegate: AppOpenDelegate by inject()

    private val args: OnBoardingActivityArgs? by lazy {
        intent.extras?.getParcelable(KEY_ARG)
    }

    private val viewModel: OnBoardingViewModel by viewModel(parameters = { parametersOf(args) })

    companion object {

        fun newIntent(
            context: Context,
            clearNotification: Boolean = false,
            showLoginFragment: Boolean = false
        ): Intent {
            val args = OnBoardingActivityArgs(
                clearNotification = clearNotification,
                showLoginFragment = showLoginFragment
            )

            return Intent(context, OnBoardingActivity::class.java)
                .apply {
                    putExtras(args.toBundle()!!)
                }
        }

        // Special action to clear cache and/or clear credentials
        fun restartApp(activity: Activity, clearNotification: Boolean = false) {
            val intent = newIntent(activity, clearNotification = clearNotification)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }

        fun showLoginScreen(activity: Activity) {
            val intent = newIntent(activity, showLoginFragment = true)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)

        viewModel.viewEvents.observe(this) {
            when (it) {
                OnBoardingViewEvents.ShowSelectLanguageFragment -> showFragment(
                    SelectLanguageFragment.newInstance(),
                    SelectLanguageFragment.TAG
                )
                is OnBoardingViewEvents.ShowMainScreen -> {
                    MainActivity.launch(this, it.pathwayId)
                    finish()
                }
                OnBoardingViewEvents.ShowOnBoardingPages -> showFragment(
                    OnBoardPagesFragment.newInstance(),
                    OnBoardPagesFragment.TAG,
                )
                OnBoardingViewEvents.ShowCourseSelectionScreen -> showFragment(
                    SelectCourseFragment.newInstance(), SelectCourseFragment.TAG
                )
                OnBoardingViewEvents.ShowLoginScreen -> showFragment(
                    LoginFragment.newInstance(),
                    LoginFragment.TAG
                )
                OnBoardingViewEvents.ShowPartnerScreen -> showFragment(
                    PartnerFragment.newInstance(),
                    PartnerFragment.TAG
                )
            }
        }

        args?.let { args ->
            appOpenDelegate.onAppOpened(this, args.clearNotification)
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container_onboarding, fragment, tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }
}