package org.navgurukul.playground.navigation

import android.content.Context
import android.content.Intent
import org.merakilearn.core.navigator.PlaygroundModuleNavigator
import org.navgurukul.playground.editor.PythonEditorActivity
import java.io.File


class PythonModuleNavigatorImpl : PlaygroundModuleNavigator {

    private var isFromCourse: Boolean = false

    override fun launchPlaygroundActivity(context: Context, code: String?,isFromCourse:Boolean): Intent {
        this.isFromCourse = isFromCourse
        return PythonEditorActivity.launch(code, context,isFromCourse)
    }

    override fun openPlaygroundWithFileContent(context: Context, file: File): Intent {
        return PythonEditorActivity.launchWithFileContent(file, context)
    }
}