package org.merakilearn.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Base64
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.WebView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.merakilearn.R
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle
import org.merakilearn.repo.ScratchRepositoryImpl
import java.io.File

class ScratchActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var scratchRepository: ScratchRepositoryImpl
    var file: File? = null
    var savedFile: Boolean = false
    var datalinkload: String = ""
    var datalinksave: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        scratchRepository = ScratchRepositoryImpl(this)
        file = intent.extras?.get("file") as File?

        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("file:///android_asset/index.html")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.addJavascriptInterface(this, "Scratch")

    }

    val data = datalinkload.toByteArray(charset("UTF-8"))
    val base64: String = Base64.encodeToString(data, Base64.DEFAULT)

    @JavascriptInterface
    fun onSave() {
        showCodeSaveDialog()
    }

    @JavascriptInterface
    fun onBack() {
        Toast.makeText(this, "Exiting Scratch", Toast.LENGTH_SHORT).show()
        finish()
        onBackPressed()
    }

    @JavascriptInterface
    fun getBase64StringFromWeb(myIds: String) {
        datalinksave = myIds
        println(datalinksave)
    }


    // Overriding WebViewClient functions
    inner class WebChromeClient : android.webkit.WebChromeClient() {

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?,
        ): Boolean {
            println("Data is passed")
            return true
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            webView.loadUrl("javascript:Scratch.getBase64StringFromWeb(globalBase64String);")
            return super.onConsoleMessage(consoleMessage)
        }

    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            file?.let { isSavedFilePresent(it) }

            if(progressBar.visibility == View.VISIBLE)
                progressBar.visibility = View.GONE
        }

    }

    fun isSavedFilePresent(file: File) {
        progressBar.visibility = View.VISIBLE
        datalinkload = Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
        webView.loadUrl("javascript:loadProjectUsingBase64('" + file.name + "','" + datalinkload + "')")
        if(progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.GONE
    }

    private fun showCodeSaveDialog() {
        val view: View = layoutInflater.inflate(R.layout.alert_save, null);
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Save Scratch File")
        alertDialog.setIcon(R.drawable.ic_scratch)
        alertDialog.setCancelable(false);
        alertDialog.setMessage("You may loose your progress if you exit without saving")

        val fileName: EditText = view.findViewById(R.id.fileName);
        if (file != null) {
            fileName.setText(file!!.name.removeSuffix(".sb3"))
            savedFile = true
        }
        alertDialog.setPositiveButton("Save") { dialog, which ->
            GlobalScope.launch(Dispatchers.Main) {
                if (fileName.text.isNotEmpty()) {
                    if (!savedFile && !scratchRepository.isFileNamePresent(fileName.text.toString() + ".sb3")) {
                        scratchRepository.saveScratchFile(datalinksave,
                            fileName.text.trim().toString(),
                            false)
                        dialog.dismiss()
                        showCodeSavedDialog()
                    } else if (savedFile) {
                        scratchRepository.saveScratchFile(datalinksave,
                            fileName.text.trim().toString(),
                            true)
                        dialog.dismiss()
                        showCodeSavedDialog()
                    } else {
                        Toast.makeText(this@ScratchActivity,
                            "This file already exists, try a different name",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ScratchActivity,
                        "File name cannot be empty",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
        alertDialog.setNegativeButton("Don't Save") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun showCodeSavedDialog(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("File Saved")
        alertDialog.setIcon(R.drawable.ic_scratch);
        alertDialog.setCancelable(true)
        alertDialog.setMessage("Your File has been saved")
        alertDialog.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}

