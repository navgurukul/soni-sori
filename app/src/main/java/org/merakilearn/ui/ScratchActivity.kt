package org.merakilearn.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
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
import org.merakilearn.R
import org.merakilearn.repo.ScratchRepositoryImpl
import org.merakilearn.util.Constants
import java.io.File


class ScratchActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var scratchRepository: ScratchRepositoryImpl
    var file: File? = null
    var savedFile: Boolean = false
    var savedFileName: String = ""
    var loadSavedFileCalled: Boolean = false
    var datalinkload: String = ""
    var datalinksave: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_scratch)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        scratchRepository = ScratchRepositoryImpl(this)
        file = intent.extras?.get(Constants.INTENT_EXTRA_KEY_FILE) as File?

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

    @JavascriptInterface
    fun onSave(myIds: String) {
        datalinksave = myIds
        showCodeSaveDialog()
    }

    @JavascriptInterface
    fun onBack() {
        Toast.makeText(this, "Exiting Scratch", Toast.LENGTH_SHORT).show()
        finish()
        onBackPressed()
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
            return super.onConsoleMessage(consoleMessage)
        }

    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            if (!loadSavedFileCalled) {
                loadSavedFileCalled = true
                loadSavedFile(file)
            }

        }

    }

    fun loadSavedFile(file: File?) {

        if (progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.GONE

        webView.loadUrl("javascript:openLoaderScreen();")

        if (file != null) {
            savedFileName = file.name
            datalinkload = Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
        } else {
            savedFileName = "defaultMerakiScratchFile.sb3"
            datalinkload = Base64.encodeToString(application.assets.open(
                "defaultMerakiScratchFile.sb3").readBytes(), Base64.DEFAULT)
        }

        webView.loadUrl("javascript:loadProjectUsingBase64('" + savedFileName + "','" + datalinkload + "')")

        Handler(Looper.getMainLooper()).postDelayed({
            webView.loadUrl("javascript:closeLoaderScreen();")
        }, 5000)

    }

    private fun showCodeSaveDialog() {
        val view: View = layoutInflater.inflate(R.layout.alert_save, null)
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle("Save Scratch File")
            setIcon(R.drawable.ic_scratch)
            setCancelable(false)
            setMessage("You may loose your progress if you exit without saving")
        }

        val fileName: EditText = view.findViewById(R.id.fileName)
        file?.let {
            with(fileName) { setText(it.name.removeSuffix(".sb3")) }
            savedFile = true
        }
        alertDialog.setPositiveButton("Save") { dialog, which ->
            GlobalScope.launch(Dispatchers.Main) {
                when {
                    fileName.text.isNotEmpty() -> {
                        when {
                            !savedFile && !scratchRepository.isFileNamePresent(fileName.text.toString() + ".sb3") -> {
                                scratchRepository.saveScratchFile(datalinksave,
                                    fileName.text.trim().toString(),
                                    false)
                                dialog.dismiss()
                                showCodeSavedDialog()
                            }
                            savedFile -> {
                                scratchRepository.saveScratchFile(datalinksave,
                                    fileName.text.trim().toString(),
                                    true)
                                dialog.dismiss()
                                showCodeSavedDialog()
                            }
                            else -> {
                                Toast.makeText(this@ScratchActivity,
                                    "This file already exists, try a different name",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(this@ScratchActivity,
                            "File name cannot be empty",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        alertDialog.setNegativeButton("Don't Save") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun showCodeSavedDialog() {
        val alertDialog = AlertDialog.Builder(this).apply {
            setTitle("File Saved")
            setIcon(R.drawable.ic_scratch)
            setCancelable(true)
            setMessage("Your File has been saved")
        }
        alertDialog.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

}