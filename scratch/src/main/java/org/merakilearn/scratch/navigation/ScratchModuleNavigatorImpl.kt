package org.merakilearn.scratch.navigation

import android.content.Context
import android.content.Intent
import org.koin.core.context.loadKoinModules
import org.merakilearn.core.navigator.ScratchModuleNavigator
import org.merakilearn.scratch.ScratchMainActivity
import org.merakilearn.scratch.di.scratchModules
import java.io.File

class ScratchModuleNavigatorImpl: ScratchModuleNavigator {

    override fun launchScratchActivity(context: Context): Intent {
        loadKoinModules(scratchModules)
        return ScratchMainActivity.launch(context)
    }

    override fun launchScratchActivityWithFile(context: Context, file: File): Intent {
        loadKoinModules(scratchModules)
        return ScratchMainActivity.launchWithFileContent(context, file)
    }

}