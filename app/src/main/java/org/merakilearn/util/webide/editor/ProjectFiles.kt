package org.merakilearn.util.webide.editor

import android.content.Context
import org.merakilearn.extension.replace

class ProjectFiles {

    companion object {

        fun readTextFromAsset(context: Context, name: String) =
            context.assets.open(name).bufferedReader().use { it.readText() }

        fun getHtml(context: Context, type: String, name: String) =
            readTextFromAsset(context, "files/$type/index.html")
                .replace(
                    "@name" to  name)

        fun getCss(context: Context, type: String) =
            readTextFromAsset(context, "files/$type/style.css")

        fun getJs(context: Context, type: String) =
            readTextFromAsset(context, "files/$type/main.js")
    }
}