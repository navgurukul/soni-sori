package org.merakilearn.util.webide.editor

import android.view.View
import com.google.android.material.snackbar.Snackbar
import org.merakilearn.extension.snack
import java.io.File

object Clipboard {

    var currentFile: File? = null
    var type = Type.COPY

    fun update(file: File, t: Type, view: View) {
        currentFile = file
        type = t

        val msg = if (t == Type.COPY) {
            "copied"
        } else {
            "moved"
        }

        view.snack("${file.name} selected to be $msg.", Snackbar.LENGTH_SHORT)
    }

    enum class Type {
        COPY, CUT
    }
}
