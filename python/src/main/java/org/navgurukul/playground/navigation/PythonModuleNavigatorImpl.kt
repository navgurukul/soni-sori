package org.navgurukul.playground.navigation

import android.content.Context
import android.content.Intent
import org.merakilearn.core.navigator.PlaygroundModuleNavigator
import org.navgurukul.playground.ui.PythonPlaygroundActivity
import java.io.File


class PythonModuleNavigatorImpl : PlaygroundModuleNavigator {

    override fun launchPlaygroundActivity(context: Context, code: String): Intent {
        return PythonPlaygroundActivity.launch(code, context)
    }

    override fun openPlaygroundWithFileContent(context: Context, file: File): Intent {
        return PythonPlaygroundActivity.launchWithFileContent(file, context)
    }
}