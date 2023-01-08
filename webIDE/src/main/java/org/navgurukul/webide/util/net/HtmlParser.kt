//package org.navgurukul.webide.util.net
//
//import io.geeteshk.hyper.util.project.ProjectManager
//import org.jsoup.Jsoup
//import org.jsoup.nodes.Document
//import timber.log.Timber
//import java.io.IOException
//
//object HtmlParser {
//
//    private fun getSoup(name: String): Document? = try {
//        Jsoup.parse(ProjectManager.getIndexFile(name), "UTF-8")
//    } catch (e: IOException) {
//        Timber.e(e)
//        null
//    }
//
//    fun getProperties(projName: String): Array<String?> {
//        val soup = getSoup(projName)
//        val properties = arrayOfNulls<String>(4)
//
//        soup?.let {
//            properties[0] = soup.head().getElementsByTag("title").text()
//            val metas = soup.head().getElementsByTag("meta")
//            for (meta in metas) {
//                val content = meta.attr("content")
//                val name = meta.attr("name")
//
//                when (name) {
//                    "author" -> properties[1] = content
//                    "description" -> properties[2] = content
//                    "keywords" -> properties[3] = content
//                }
//            }
//        }
//
//        return properties
//    }
//}
