package org.merakilearn.util.webide.Projects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import org.merakilearn.R
import org.merakilearn.extension.copyInputStreamToFile
import org.merakilearn.extension.snack
import org.merakilearn.util.webide.ROOT_PATH
import org.merakilearn.util.webide.adapter.ProjectAdapter
import org.merakilearn.util.webide.editor.ProjectFiles
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

object ProjectManager {

    val TYPES = arrayOf("Default")

    fun generate(
        context: Context,
        name: String,
        stream: InputStream?,
        adapter: ProjectAdapter,
        view: View,
        type: Int
    ): String {
        var nameNew = name
        var counter = 1
        while (File(context.ROOT_PATH() + File.separator + nameNew).exists()) {
            nameNew = "$name($counter)"
            counter++
        }

        var status = false
        when (type) {
            0 -> status = generateDefault(context, nameNew, stream)
        }

        if (status) {
            adapter.insert(nameNew)
            view.snack(R.string.project_success, Snackbar.LENGTH_SHORT)
        } else {
            view.snack(R.string.project_fail, Snackbar.LENGTH_SHORT)
        }
        return nameNew
    }

    private fun generateDefault(
        context: Context,
        name: String,
        stream: InputStream?
    ): Boolean {
        val projectFile = File("${context.ROOT_PATH()}/$name")
        val cssFile = File(projectFile, "css")
        val jsFile = File(projectFile, "js")
        try {
            projectFile.mkdirs()
            File(projectFile, "images").mkdirs()
            File(projectFile, "fonts").mkdirs()
            cssFile.mkdirs()
            jsFile.mkdirs()

            File(projectFile, "index.html").writeText(
                ProjectFiles.getHtml(
                    context,
                    "default",
                    name
                )
            )
            File(cssFile, "style.css").writeText(ProjectFiles.getCss(context, "default"))
            File(jsFile, "main.js").writeText(ProjectFiles.getJs(context, "default"))

            if (stream == null) {
                copyIcon(context, name)
            } else {
                copyIcon(context, name, stream)
            }
        } catch (e: IOException) {
            Timber.e(e)
            Log.i("TAG", e.message.toString())
            e.printStackTrace()
            return false
        }

        return true
    }

    fun importProject(
        context: Context,
        fileStr: String,
        name: String,
        author: String,
        description: String,
        keywords: String,
        adapter: ProjectAdapter,
        view: View
    ) {
        val file = File(fileStr)
        var nameNew = name
        var counter = 1
        while (File(context.ROOT_PATH() + File.separator + nameNew).exists()) {
            nameNew = file.name + "(" + counter + ")"
            counter++
        }

        val outFile = File(context.ROOT_PATH() + File.separator + nameNew)
        try {
            outFile.mkdirs()
            file.copyRecursively(outFile)

            val index = File(outFile, "index.html")
            if (!index.exists()) {
                index.writeText(
                    ProjectFiles.getHtml(
                        context,
                        "import",
                        nameNew
                    )
                )
            }
        } catch (e: IOException) {
            Timber.e(e)
            view.snack(R.string.project_fail, Snackbar.LENGTH_SHORT)
            return
        }

        adapter.insert(nameNew)
        view.snack(R.string.project_success, Snackbar.LENGTH_SHORT)
    }

    fun isValid(context: Context, string: String): Boolean = getIndexFile(context, string) != null

    fun deleteProject(context: Context, name: String) {
        try {
            File("${context.ROOT_PATH()}/$name").deleteRecursively()
        } catch (e: IOException) {
            Timber.e(e)
        }

    }

    private fun getFaviconFile(dir: File) =
        dir.walkTopDown().filter { it.name == "favicon.ico" }.firstOrNull()

    fun getIndexFile(context: Context, project: String) =
        File("${context.ROOT_PATH()}/$project").walkTopDown()
            .filter { it.name == "index.html" }.firstOrNull()

    fun getRelativePath(context: Context, file: File, projectName: String) =
        file.path.replace(File("${context.ROOT_PATH()}/$projectName").path, "")

    fun getFavicon(context: Context, name: String): Bitmap {
        val faviconFile = getFaviconFile(File(context.ROOT_PATH() + File.separator + name))
        return if (faviconFile != null) {
            BitmapFactory.decodeFile(faviconFile.path)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher)
        }
    }

    private fun copyIcon(context: Context, name: String) {
        try {
            val manager = context.assets
            val stream = manager.open("web/favicon.ico")
            val output =
                File(context.ROOT_PATH() + File.separator + name + File.separator + "images" + File.separator + "favicon.ico")
            output.copyInputStreamToFile(stream)
            stream.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun copyIcon(context: Context, name: String, stream: InputStream) {
        try {
            val output =
                File(context.ROOT_PATH() + File.separator + name + File.separator + "images" + File.separator + "favicon.ico")
            output.copyInputStreamToFile(stream)
            stream.close()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun isBinaryFile(f: File): Boolean {
        var result = 0
        try {
            val `in` = FileInputStream(f)
            var size = `in`.available()
            if (size > 1024) size = 1024
            val data = ByteArray(size)
            result = `in`.read(data)
            `in`.close()

            var ascii = 0
            var other = 0

            for (b in data) {
                if (b < 0x09) return true

                if (b.toInt() == 0x09 || b.toInt() == 0x0A || b.toInt() == 0x0C || b.toInt() == 0x0D)
                    ascii++
                else if (b in 0x20..0x7E)
                    ascii++
                else
                    other++
            }

            return other != 0 && 100 * other / (ascii + other) > 95

        } catch (e: Exception) {
            Timber.e(e, result.toString())
        }

        return true
    }

    fun isImageFile(f: File): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(f.absolutePath, options)
        return options.outWidth != -1 && options.outHeight != -1
    }

    fun importFile(context: Context, name: String, fileUri: Uri, fileName: String): Boolean {
        try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val output =
                File(context.ROOT_PATH() + File.separator + name + File.separator + fileName)
            output.copyInputStreamToFile(inputStream!!)
            inputStream.close()
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }

        return true
    }

    fun humanReadableByteCount(bytes: Long): String {
        val unit = 1000
        if (bytes < unit) return bytes.toString() + " B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "kMGTPE"[exp - 1] + ""
        return String.format(
            Locale.getDefault(),
            "%.1f %sB",
            bytes / Math.pow(unit.toDouble(), exp.toDouble()),
            pre
        )
    }
}