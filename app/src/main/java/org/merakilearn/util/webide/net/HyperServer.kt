package org.merakilearn.util.webide.net

import fi.iki.elonen.NanoHTTPD
import org.merakilearn.util.webide.Constants
import org.merakilearn.util.webide.project.ProjectManager
import timber.log.Timber
import java.io.File
import java.io.IOException

class HyperServer(private val project: String) : NanoHTTPD(PORT_NUMBER) {

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        var uri = session.uri
        val mimeType = getMimeType(uri)

        if (uri == "/") {
            val indexFile = ProjectManager.getIndexFile(project)
            uri = "/${ProjectManager.getRelativePath(indexFile!!, project)}"
        }

        return try {
            NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, mimeType, File("${Constants.HYPER_ROOT}/$project$uri").readText())
        } catch (e: IOException) {
            Timber.e(e)
            NanoHTTPD.newFixedLengthResponse(e.toString())
        }
    }

    private fun getMimeType(uri: String) = MIME_TYPES.keys.firstOrNull { uri.endsWith(it) }
            ?.let { MIME_TYPES[it] } ?: NanoHTTPD.MIME_HTML

    companion object {

        private val MIME_TYPES = mapOf(
                "css" to "text/css",
                "js" to "text/javascript",
                "ico" to "image/x-icon",
                "png" to "image/png",
                "jpg" to "image/jpeg",
                "jpeg" to "image/jpeg",
                "svg" to "image/svg+xml",
                "bmp" to "image/bmp",
                "gif" to "image/gif",
                "ttf" to "application/x-font-ttf",
                "otf" to "application/x-font-opentype",
                "woff" to "application/font-woff",
                "woff2" to "application/font-woff2",
                "eot" to "application/vnd.ms-fontobject",
                "sfnt" to "application/font-sfnt",
                "json" to "application/json",
                "txt" to "text/plain",
                "xml" to "text/xml")

        const val PORT_NUMBER = 8080
    }
}
