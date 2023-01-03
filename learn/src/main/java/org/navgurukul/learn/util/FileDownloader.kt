package org.navgurukul.learn.util

import android.content.Context
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


object FileDownloader {
    private const val MEGABYTE = 1024 * 1024
    fun downloadFile(context: Context?, fileUrl: String?, directory: File?): Boolean {
        try {
            val url = URL(fileUrl)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.getInputStream()
            val fileOutputStream = FileOutputStream(directory)
            val totalSize: Int = urlConnection.contentLength
            val buffer = ByteArray(FileDownloader.MEGABYTE)
            var bufferLength = 0
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()

//            val fileUri: Uri = FileProvider.getUriForFile(context!!, "org.navgurukul.learn.provider", directory!!)
//            val intentShareFile = Intent(Intent.ACTION_VIEW)
//            intentShareFile.setDataAndType(fileUri, "application/pdf")
//            intentShareFile.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//            intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            context.startActivity(intentShareFile)

            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}