package org.navgurukul.saral

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.navgurukul.saral.databinding.ActivityOnBoardingBinding
import org.navgurukul.saral.ui.onboarding.SplashFragment
import org.navgurukul.saral.ui.onboarding.WelcomeFragment
import org.navgurukul.saral.util.AppUtils

class OnBoardingActivity : AppCompatActivity() {
    private val splashTime: Long = 1500L
    private lateinit var mBinding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        showFragment(SplashFragment.newInstance(), SplashFragment.TAG)
        initDelayedStart()
    }

    private fun initDelayedStart() {
        Handler().postDelayed({
            startDestinationActivity()
        }, splashTime)
    }

    private fun startDestinationActivity() {
        if (AppUtils.isUserLoggedIn(this))
            MainActivity.launch(this)
        else
            showFragment(WelcomeFragment.newInstance(), WelcomeFragment.TAG)
    }

    private fun showFragment(fragment: Fragment, tag: String?) {
        AppUtils.addFragmentToActivity(
            supportFragmentManager,
            fragment,
            R.id.fragment_container_onboarding,
            tag
        )
    }
}