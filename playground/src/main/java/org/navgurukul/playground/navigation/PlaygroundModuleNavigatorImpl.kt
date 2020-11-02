package org.navgurukul.playground.navigation

import android.content.Context
import android.content.Intent
import org.merakilearn.core.navigator.PlaygroundModuleNavigator
import org.navgurukul.playground.ui.PlaygroundActivity


class PlaygroundModuleNavigatorImpl : PlaygroundModuleNavigator {

    override fun launchPlaygroundActivity(context: Context, code: String): Intent {
        return PlaygroundActivity.launch(code, context)
    }
}