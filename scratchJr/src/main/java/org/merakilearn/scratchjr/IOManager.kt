package org.merakilearn.scratchjr

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.merakilearn.scratchjr.ScratchJrUtil.copyFile
import org.merakilearn.scratchjr.ScratchJrUtil.extension
import org.merakilearn.scratchjr.ScratchJrUtil.readJson
import org.merakilearn.scratchjr.ScratchJrUtil.removeFile
import org.merakilearn.scratchjr.ScratchJrUtil.unzip
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.UUID


/**
 * Manages file storage for ScratchJr.
 *
 * Also interfaces with the DatabaseManager to clean assets.
 *
 * @author markroth8
 */
class IOManager(private val _application: ScratchJrActivity) {
    private val _databaseManager: DatabaseManager? = _application.databaseManager

    /** Cache of key to base64-encoded media value  */
    private val _mediaStrings: MutableMap<String, String> = HashMap()

    /**
     * clean up sharing zips in cache dir.
     */
    fun cleanZips() {
        val suffix = extension
        val dir = _application.cacheDir
        Log.i(LOG_TAG, "Cleaning files of type '" + suffix + "' in dir: " + dir.absolutePath)
        for (file in dir.listFiles()) {
            val filename = file.name
            Log.i(LOG_TAG, filename)
            if (!filename.endsWith(suffix)) {
                continue
            }
            Log.i(LOG_TAG, "removing file: $filename")
            file.delete()
        }
    }

    /**
     * Clean any assets that are not referenced in the database
     *
     * @param fileType The extension of the type of file to clean
     */
    @Throws(IOException::class)
    fun cleanAssets(fileType: String) {
        val suffix = ".$fileType"
        Log.i(LOG_TAG, "Cleaning files of type '$fileType'")
        val dir = _application.filesDir
        for (file in dir.listFiles()) {
            val filename = file.name
            if (filename.endsWith(suffix)) {
                try {
                    var statement = "SELECT ID FROM PROJECTS WHERE JSON LIKE ?"
                    var values = arrayOf("%$filename%").map { it as String? }.toTypedArray() // Cast to Array<String?>
                    var rows: JSONArray = _databaseManager?.query(statement, values) ?: JSONArray()
                    if (rows.length() > 0) continue

                    statement = "SELECT ID FROM USERSHAPES WHERE MD5 = ?"
                    values = arrayOf(filename).map { it as String? }.toTypedArray() // Cast to Array<String?>
                    rows = _databaseManager?.query(statement, values) ?: JSONArray()
                    if (rows.length() > 0) continue

                    statement = "SELECT ID FROM USERBKGS WHERE MD5 = ?"
                    rows = _databaseManager?.query(statement, arrayOf(filename).map { it as String? }.toTypedArray()) ?: JSONArray()
                    if (rows.length() > 0) continue

                    Log.i(
                        LOG_TAG,
                        "Deleting because not found anywhere: '$filename'"
                    )
                    file.delete()
                } catch (e: JSONException) {
                    // log and continue searching
                    Log.e(LOG_TAG, "While searching for resources to delete", e)
                } catch (e: DatabaseException) {
                    // log and continue searching
                    Log.e(LOG_TAG, "While searching for resources to delete", e)
                }
            }
        }
    }


    /** Sets the file with the given name to the given contents  */
    @Throws(IOException::class)
    fun setFile(filename: String, base64ContentStr: String?): String {
        val content = Base64.decode(base64ContentStr, Base64.NO_WRAP)
        val out = _application.openFileOutput(filename, Context.MODE_PRIVATE)
        try {
            out.write(content)
        } finally {
            out.close()
        }
        return filename
    }

    /** Gets a base64-encoded view of the contents of the given file  */
    @Throws(IOException::class)
    fun getFile(filename: String?): String {
        val result: String
        val `in`: InputStream = _application.openFileInput(filename)
        try {
            val bos = ByteArrayOutputStream()
            var len: Int
            val buffer = ByteArray(1024)
            while ((`in`.read(buffer).also { len = it }) != -1) {
                bos.write(buffer, 0, len)
            }
            bos.close()
            val data = bos.toByteArray()
            result = Base64.encodeToString(data, Base64.NO_WRAP)
        } finally {
            `in`.close()
        }
        return result
    }

    /**
     * Returns the media data associated with the given filename and return the result base64-encoded.
     */
    @Throws(IOException::class)
    fun getMedia(filename: String?): String {
        val result: String
        val `in`: InputStream = _application.openFileInput(filename)
        try {
            val bos = ByteArrayOutputStream()
            var len: Int
            val buffer = ByteArray(1024)
            while ((`in`.read(buffer).also { len = it }) != -1) {
                bos.write(buffer, 0, len)
            }
            bos.close()
            val data = bos.toByteArray()
            result = Base64.encodeToString(data, Base64.NO_WRAP)
        } finally {
            `in`.close()
        }
        return result
    }

    /**
     * Allows incremental loading of large resources
     */
    fun getMediaData(key: String, offset: Int, length: Int): String {
        return _mediaStrings[key]!!.substring(offset, offset + length)
    }

    @Throws(IOException::class)
    fun getMediaLen(file: String?, key: String): Int {
        val value = getMedia(file)
        _mediaStrings[key] = value
        return value.length
    }

    fun getMediaDone(filename: String) {
        _mediaStrings.remove(filename)
    }

    /**
     * Store the given content in a file whose filename is constructed using the md5 sum of the base64 content string
     * followed by the given extension, and return the filename.
     *
     * @param base64ContentStr Base64-encoded content to store in the file
     * @param extension The extension of the filename to store to
     * @return The filename of the file that was saved.
     * @throws IOException If there was an error saving the file.
     */
    @Throws(IOException::class)
    fun setMedia(base64ContentStr: String, extension: String): String {
        val md5Sum = md5(base64ContentStr)
        val filename = "$md5Sum.$extension"
        val content = Base64.decode(base64ContentStr, Base64.NO_WRAP)
        val out = _application.openFileOutput(filename, Context.MODE_PRIVATE)
        try {
            out.write(content)
        } finally {
            out.close()
        }
        return filename
    }

    /**
     * Writes the given base64-encoded content to a filename with the name key.ext.
     */
    @Throws(IOException::class)
    fun setMediaName(base64ContentStr: String?, key: String, ext: String): String {
        val md5 = "$key.$ext"
        return writeFile(md5, base64ContentStr)
    }

    /**
     * Decodes the given base64-encoded data and writes it to the file with the given filename
     * in the application's persistent store.
     */
    @Throws(IOException::class)
    fun writeFile(filename: String, base64ContentStr: String?): String {
        val content = Base64.decode(base64ContentStr, Base64.NO_WRAP)
        val out = _application.openFileOutput(filename, Context.MODE_PRIVATE)
        try {
            out.write(content)
        } finally {
            out.close()
        }
        return filename
    }

    /**
     * Returns the MD5 sum of the provided content.
     *
     * @param content The content to sum
     * @return A string representation of the MD5 sum
     */
    fun md5(content: String): String {
        val digester: MessageDigest
        try {
            digester = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
        digester.update(content.toByteArray())
        val digest = digester.digest()
        return bytesToHexString(digest)
    }

    /**
     * Removes (deletes) a file with a given file name.
     *
     * @param filename The file to remove
     * @return True if the file was successfully removed; else false
     * @throws IOException If there was an error removing the file
     */
    @Throws(IOException::class)
    fun remove(filename: String?): Boolean {
        return _application.deleteFile(filename)
    }

    @Throws(JSONException::class, IOException::class, DatabaseException::class)
    fun receiveProject(activity: ScratchJrActivity, uri: Uri?) {
        // open database first
        if (_databaseManager?.isOpen == false) {
            _databaseManager.open()
        }
        val tempDir =
            File(activity.cacheDir.toString() + File.separator + UUID.randomUUID().toString())
        tempDir.mkdir()
        val entries = unzip(
            activity.contentResolver.openInputStream(
                uri!!
            ), tempDir.path
        )
        if (entries.isEmpty()) {
            Log.e(LOG_TAG, "no entries found")
            // no files
            removeFile(tempDir)
            return
        }
        // read project json
        val json = readJson("$tempDir/project/data.json")
        val projectData = json.optJSONObject("json")
        val projectJson = JSONObject()
        projectJson.put("isgift", "1")
        projectJson.put("deleted", "NO")
        projectJson.put("json", projectData.toString())
        val thumbnail = json.optJSONObject("thumbnail")
        projectJson.put("thumbnail", thumbnail.toString())
        projectJson.put("version", "iOSv01")
        projectJson.put("name", json.optString("name"))
        _databaseManager?.insert("projects", projectJson)

        val spriteMap = HashMap<String, JSONObject>()
        val pages = projectData.optJSONArray("pages")
        for (i in 0 until pages.length()) {
            val pageName = pages.optString(i) ?: continue
            val page = projectData.optJSONObject(pageName)
            val spriteNames = page.optJSONArray("sprites")
            for (j in 0 until spriteNames.length()) {
                val spriteName = spriteNames.optString(j)
                val sprite = page.optJSONObject(spriteName)
                spriteMap[sprite.optString("md5")] = sprite
            }
        }
        for (i in entries.indices) {
            val entry = entries[i] ?: continue
            if (!(entry.endsWith(".png") || entry.endsWith(".wav") || entry.endsWith(".svg"))) {
                continue
            }
            // copy file to target file
            val sourceFile = File(tempDir.toString() + File.separator + entry)

            val fileName = sourceFile.name
            val targetFile = File(activity.filesDir.path + File.separator + fileName)
            if (!targetFile.exists()) {
                copyFile(sourceFile, targetFile)
            }
            val folderName = sourceFile.parentFile.name
            if ("thumbnails" == folderName || "sounds" == folderName) {
                continue
            }
            val table = if ("characters" == folderName) "usershapes" else "userbkgs"
            val statement = String.format("SELECT id FROM %s WHERE md5 = ?", table)
            val rows: JSONArray = _databaseManager?.query(statement, arrayOf(fileName)) ?: JSONArray()
            if (rows.length() > 0) {
                Log.e(LOG_TAG, "asset for $fileName exists")
                continue
            }
            val pngName = fileName.replace(".svg", ".png")
            val asset = JSONObject()
            asset.put("version", "iOSv01")
            asset.put("md5", fileName)
            asset.put("altmd5", pngName)
            asset.put("width", "480")
            asset.put("height", "360")
            asset.put(
                "ext",
                fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            )
            if ("characters" == folderName) {
                val sprite = spriteMap[fileName] ?: continue
                asset.put("width", sprite.optString("w"))
                asset.put("height", sprite.optString("h"))
                asset.put("scale", sprite.optString("scale"))
                asset.put("name", sprite.optString("name"))
            }
            val png = File(activity.filesDir.path + File.separator + pngName)
            if (!png.exists()) {
                val js = String.format(
                    "ScratchJr.makeThumb('%s', %s, %s)",
                    fileName,
                    asset.optString("width"),
                    asset.optString("height")
                )
                Log.d(LOG_TAG, js)
                activity.runJavaScript(js)
            }
            _databaseManager?.insert(table, asset)
        }
        // clean up
        removeFile(tempDir)
        // refresh lobby
        activity.runJavaScript("Lobby.refresh();")
    }

    companion object {
        private const val LOG_TAG = "ScratchJr.IOManager"
        private val HEX_ARRAY = "0123456789abcdef".toCharArray()

        /**
         * Borrowed from http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
         */
        fun bytesToHexString(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v = bytes[j].toInt() and 0xFF
                hexChars[j * 2] = HEX_ARRAY[v ushr 4]
                hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
            }
            return String(hexChars)
        }
    }
}