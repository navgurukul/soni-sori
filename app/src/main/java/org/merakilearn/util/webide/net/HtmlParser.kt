package org.merakilearn.util.webide.net

import android.content.Context
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.merakilearn.util.webide.Projects.ProjectManager
import timber.log.Timber
import java.io.IOException

object HtmlParser {

    private fun getSoup(context: Context,name: String): Document? = try {
        Jsoup.parse(ProjectManager.getIndexFile(context,name), "UTF-8")
    } catch (e: IOException) {
        Timber.e(e)
        null
    }

    fun getProperties(context: Context,projName: String): Array<String?> {
        val soup = getSoup(context,projName)
        val properties = arrayOfNulls<String>(4)

        soup?.let {
            properties[0] = soup.head().getElementsByTag("title").text()
            val metas = soup.head().getElementsByTag("meta")
            for (meta in metas) {
                val content = meta.attr("content")
                val name = meta.attr("name")

                when (name) {
                    "author" -> properties[1] = content
                    "description" -> properties[2] = content
                    "keywords" -> properties[3] = content
                }
            }
        }

        return properties
    }
}