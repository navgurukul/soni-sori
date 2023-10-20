package org.navgurukul.learn.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

object PdfGenerateUtils {

    fun generatePDF(context: Context, pdfUrl: String) {
        val download = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val pdfUri = Uri.parse(pdfUrl)
        val getPdf = DownloadManager.Request(pdfUri)
        getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        download.enqueue(getPdf)
        Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show()
    }

    fun download(context: Context, pdfUrl: String, pdfView: ImageView) {
        val fileUrl = pdfUrl // -> http://maven.apache.org/maven-1.x/maven.pdf
        val fileName = "certificate.pdf" // -> maven.pdf
        val extStorageDirectory = context?.filesDir
        val folder = File(extStorageDirectory, "certificate")
        folder.mkdir()
        val pdfFile = File(folder, fileName)
        try {
            pdfFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (FileDownloader.downloadFile(context, fileUrl, pdfFile)) {
            showCertificate(context, pdfView)
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showCertificate(context: Context, pdfView: ImageView) {

        val pdfQuality = PdfQuality.NORMAL

        val fileName = "certificate.pdf" // -> maven.pdf
        val extStorageDirectory = context?.filesDir
        val folder = File(extStorageDirectory, "certificate")
        val mCardStmtFile = File(folder, fileName)

        context.let {

            // We will get a page from the PDF file by calling openPage
            val fileDescriptor = ParcelFileDescriptor.open(
                mCardStmtFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )

            val mPdfRenderer = PdfRenderer(fileDescriptor)
            ///mPdfRenderer.pageCount
            val mPdfPage = mPdfRenderer.openPage(0)

            // Create a new bitmap and render the page contents on to it
            val bitmap = Bitmap.createBitmap(
                mPdfPage.width * pdfQuality.ratio,
                mPdfPage.height * pdfQuality.ratio,
                Bitmap.Config.ARGB_8888
            )

            mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Set the bitmap in the ImageView so we can view it
            CoroutineScope(Dispatchers.Main).launch {
                pdfView.setImageBitmap(bitmap)
            }
        }
    }

    fun showShareIntent(context: Context, pdfUrl: String) {
        try {
            val fileName = "certificate.pdf" // -> maven.pdf
            val extStorageDirectory = context?.filesDir
            val folder = File(extStorageDirectory, "certificate")
            val file = File(folder, fileName)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "org.merakilearn.provider",
                file
            )

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            context.startActivity(shareIntent)
        } catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(context, "There is some issue to share", Toast.LENGTH_LONG).show()
        }

    }
}