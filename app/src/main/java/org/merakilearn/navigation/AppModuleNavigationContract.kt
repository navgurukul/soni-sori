package org.merakilearn.navigation

import android.content.Context
import android.content.Intent
import org.merakilearn.MainActivity
import org.merakilearn.ui.onboarding.OnBoardingActivity
import org.merakilearn.core.navigator.AppModuleNavigator

class AppModuleNavigationContract: AppModuleNavigator {

    override fun launchIntentForHomeActivity(context: Context, clearNotification: Boolean): Intent {
        return MainActivity.newIntent(context, clearNotification)
    }

    override fun launchIntentForOnBoardingActivity(
        context: Context,
        clearNotification: Boolean
    ): Intent {
        return OnBoardingActivity.newIntent(context, clearNotification)
    }
}