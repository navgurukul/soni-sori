package org.merakilearn.scratchjr

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


/**
 * General utility class with static utility methods.
 *
 * @author markroth8
 */
object ScratchJrUtil {
    private const val BUFFER_SIZE = 2048

    /**
     * Convert the given JSONArray to an array of Strings.
     */
    @Throws(JSONException::class)
    fun jsonArrayToStringArray(values: JSONArray): Array<String?> {
        val result = arrayOfNulls<String>(values.length())
        for (i in 0 until values.length()) {
            result[i] = values.getString(i)
        }
        return result
    }

    val extension: String
        /**
         * get sharing file extension based on APPLICATION_ID
         * @return extension
         */
        get() {
            val extension = if (BuildConfig.APPLICATION_ID == "org.pbskids.scratchjr") {
                ".psjr"
            } else {
                ".sjr"
            }
            return extension
        }

    val mimeType: String
        /**
         * get sharing file mime-type based on APPLICATION_ID
         * @return mime type
         */
        get() {
            val mimetype = if (BuildConfig.APPLICATION_ID == "org.pbskids.scratchjr") {
                "application/x-pbskids-scratchjr-project"
            } else {
                "application/x-scratchjr-project"
            }
            return mimetype
        }

    /**
     * remove file or folder
     * if `file` is a folder, it will be cleaned up before removing
     * @param file to be removed
     */
    fun removeFile(file: File) {
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                removeFile(f)
            }
        }
        file.delete()
    }

    /**
     * Copy file from a location to target location
     *
     * @param sourceLocation file path to be copied
     * @param targetLocation file path bo be copied to
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(sourceLocation: File?, targetLocation: File?) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val out: OutputStream = FileOutputStream(targetLocation)

        // Copy the bits from instream to outstream
        val buf = ByteArray(BUFFER_SIZE)
        var len: Int
        while ((`in`.read(buf).also { len = it }) > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }

    /**
     * zip a folder to target location
     * @param projectPath project folder to compress
     * @param toLocation the target location to save the zip
     * @return successful or not
     */
    fun zipProject(projectPath: String?, toLocation: String?): Boolean {
        val sourceFile = File(projectPath)
        try {
            // we don't need to remove file at `toLocation`
            // because `FileOutputStream` will overwrite the file
            // if no `append` parameter is passed
            val dest = FileOutputStream(toLocation)
            val out = ZipOutputStream(BufferedOutputStream(dest))
            zipSubFolder(out, sourceFile, sourceFile.parent.length)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /*
     * Zips a subfolder
     */
    @Throws(IOException::class)
    private fun zipSubFolder(out: ZipOutputStream, folder: File, basePathLength: Int) {
        val fileList = folder.listFiles()
        var origin: BufferedInputStream
        for (file in fileList) {
            if (file.isDirectory) {
                zipSubFolder(out, file, basePathLength)
            } else {
                val data = ByteArray(BUFFER_SIZE)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath
                    .substring(basePathLength)
                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, BUFFER_SIZE)
                val entry = ZipEntry(relativePath)
                entry.time = file.lastModified() // to keep modification time after unzipping
                out.putNextEntry(entry)
                var count: Int
                while ((origin.read(data, 0, BUFFER_SIZE).also {
                        count = it
                    }) != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    private fun getLastPathComponent(filePath: String): String {
        val segments = filePath.split(File.separator.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (segments.size == 0) {
            return ""
        }
        return segments[segments.size - 1]
    }

    fun unzip(inputStream: InputStream?, toPath: String): List<String> {
        val entries: MutableList<String> = ArrayList()
        val zin = ZipInputStream(inputStream)
        try {
            var ze: ZipEntry
            while ((zin.nextEntry.also { ze = it }) != null) {
                val path = toPath + File.separator + ze.name
                val unzipFile = File(path)
                if (ze.isDirectory) {
                    if (!unzipFile.isDirectory) {
                        unzipFile.mkdirs()
                    }
                    continue
                }
                val folder = unzipFile.parentFile
                if (!folder.isDirectory) {
                    folder.mkdirs()
                }
                val fout = FileOutputStream(path, false)
                val bout = BufferedOutputStream(fout)
                try {
                    val buffer = ByteArray(1024)
                    var read: Int
                    while ((zin.read(buffer).also { read = it }) != -1) {
                        bout.write(buffer, 0, read)
                    }
                    bout.flush()
                    zin.closeEntry()
                    entries.add(ze.name)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    fout.close()
                    bout.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            zin.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return entries
    }

    @Throws(IOException::class, JSONException::class)
    fun readJson(path: String?): JSONObject {
        val data: ByteArray
        val `in`: InputStream = FileInputStream(File(path))

        try {
            val bos = ByteArrayOutputStream()
            var len: Int
            val buffer = ByteArray(1024)
            while ((`in`.read(buffer).also { len = it }) != -1) {
                bos.write(buffer, 0, len)
            }
            bos.close()
            data = bos.toByteArray()
        } finally {
            `in`.close()
        }
        return JSONObject(String(data))
    }
}