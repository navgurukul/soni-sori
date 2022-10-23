package org.merakilearn.scratch

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.merakilearn.repo.ScratchRepositoryImpl
import java.io.File


class ScratchMainActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var scratchRepository: ScratchRepositoryImpl
    lateinit var webViewClient: WebViewClient
    var file: File? = null
    var savedFile: Boolean = false
    var datalinkload: String = ""
    var datalinksave: String = ""


    companion object {
        fun launch(context: Context): Intent {
            val intent = Intent(context, ScratchMainActivity::class.java)
            return intent
        }

        fun launchWithFileContent(context: Context, file: File): Intent {
            val intent = Intent(context, ScratchMainActivity::class.java)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch_main)

        scratchRepository = ScratchRepositoryImpl(this)
        file = intent.extras?.get("file") as File?

        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar3)
        webViewClient = WebViewClient()
        webView.webViewClient = webViewClient
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("file:///android_asset/index.html")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
//        webView.settings.allowUniversalAccessFromFileURLs = true
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
        Toast.makeText(this,"Exiting Scratch",Toast.LENGTH_SHORT).show()
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
//            webView.loadUrl("javascript:Scratch.getBase64StringFromWeb(globalBase64String);")
            progressBar.visibility = View.GONE
        }

    }

    fun isSavedFilePresent(file: File) {
        progressBar.visibility = View.VISIBLE
        datalinkload = Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
        webView.loadUrl("javascript:loadProjectUsingBase64('" + file.name + "','" + datalinkload + "')")
        progressBar.visibility = View.GONE

    }

    private fun showCodeSaveDialog() {
        val view: View = layoutInflater.inflate(R.layout.alert_save, null);
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Save Scratch File");
        alertDialog.setIcon(R.drawable.ic_scratch);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("You may loose your progress if you exit without saving")

        val progressBar2: ProgressBar = view.findViewById(R.id.fileSaveProgressBar)
        val fileName: EditText = view.findViewById(R.id.fileName);
        if (file != null) {
            fileName.setText(file!!.name.removeSuffix(".sb3"))
            savedFile = true
        }
        alertDialog.setPositiveButton("Save") { dialog, which ->
            GlobalScope.launch(Dispatchers.Main) {
                if (fileName.text.isNotEmpty()) {
                    progressBar2.visibility = View.VISIBLE
                    if (!savedFile && !scratchRepository.isFileNamePresent(fileName.text.toString() + ".sb3")) {
                        scratchRepository.saveScratchFile(datalinksave,
                            fileName.text.trim().toString(),
                            false)
                        progressBar2.visibility = View.INVISIBLE
                        dialog.dismiss()
                    } else if(savedFile){
                        scratchRepository.saveScratchFile(datalinksave,
                            fileName.text.trim().toString(),
                            true)
                        dialog.dismiss()
                        progressBar2.visibility = View.INVISIBLE
                    }
                    else {
                        Toast.makeText(this@ScratchMainActivity,
                            "This file already exists, try a different name",
                            Toast.LENGTH_SHORT).show()
                        progressBar2.visibility = View.INVISIBLE
                    }
                } else{
                    Toast.makeText(this@ScratchMainActivity,
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
}