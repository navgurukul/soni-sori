package org.merakilearn.util.webide.editor

import android.content.Context
import org.merakilearn.extension.replace

class ProjectFiles {

    companion object {

        fun readTextFromAsset(context: Context, name: String) =
                context.assets.open(name).bufferedReader().use { it.readText() }

        fun getHtml(context: Context, type: String, name: String, author: String, description: String, keywords: String) =
                readTextFromAsset(context, "files/$type/index.html")
                        .replace(
                                "@name" to  name,
                                "@author" to author,
                                "@description" to description,
                                "@keywords" to keywords)

        fun getCss(context: Context, type: String) =
                readTextFromAsset(context, "files/$type/style.css")

        fun getJs(context: Context, type: String) =
                readTextFromAsset(context, "files/$type/main.js")
    }
}
