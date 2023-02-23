package org.merakilearn.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.merakilearn.R
import org.merakilearn.datasource.UserRepo
import org.merakilearn.repo.ScratchRepositoryImpl
import org.merakilearn.repo.ScratchViewModel
import org.merakilearn.util.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


class ScratchActivity : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var scratchRepository: ScratchRepositoryImpl
    lateinit var scratchViewModel: ScratchViewModel

    private val userRepo: UserRepo by inject()
    lateinit var myRequest: PermissionRequest

    var file: File? = null
    var savedFile: Boolean = false
    var savedFileName: String = ""
    var loadSavedFileCalled: Boolean = false
    var datalinkload: String = ""
    var datalinksave: String = ""
    var datalinksave2: String = ""
    var s3url : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_scratch)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        scratchRepository = ScratchRepositoryImpl(this)
        file = intent.extras?.get(Constants.INTENT_EXTRA_KEY_FILE) as File?

//        s3url = intent.getStringExtra(Constants.INTENT_EXTRA_KEY_FILE).toString()
//        file = convertBase64StringToFile(datalinksave2)

        scratchViewModel = ScratchViewModel(userRepo)

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

    fun onSave(globalBase64String: String) {
        datalinksave = globalBase64String
        showCodeSaveDialog()
    }

    @JavascriptInterface
    fun onBack() {
        Toast.makeText(this, "Exiting Scratch", Toast.LENGTH_SHORT).show()
        finish()
        onBackPressed()
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }

    fun askForPermission(origin: String, permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(applicationContext,
                permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            myRequest.grant(myRequest.resources)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            101 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    myRequest.grant(myRequest.resources)
                }
            }
        }
    }

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

        override fun onPermissionRequest(request: PermissionRequest) {
            myRequest = request
            for (permission in request.resources) {
                when (permission) {
                    "android.webkit.resource.AUDIO_CAPTURE" -> {
                        askForPermission(request.origin.toString(),
                            Manifest.permission.RECORD_AUDIO,
                            101)
                    }
                }
            }
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
            datalinkload = datalinksave2

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
                                val projectName = fileName.text.trim().toString()
                                val isSuccess = withContext(Dispatchers.IO){
                                    scratchViewModel.postFile(datalinksave, projectName, false)
                                }
                                if(isSuccess){
                                    dialog.dismiss()
                                    showCodeSavedDialog()
                                }
                                else{
                                    dialog.dismiss()
                                }

                            }
                            savedFile -> {
                                val projectName = fileName.text.trim().toString()
                                val isSuccess = withContext(Dispatchers.IO){ scratchViewModel.postFile(datalinksave,projectName, true)
                                }

                                if(isSuccess){
                                    dialog.dismiss()
                                    showCodeSavedDialog()
                                }
                                else{
                                    dialog.dismiss()
                                }
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

//    private fun convertBase64StringToFile(base64String: String): File {
//        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
//        val fileContent = decodedBytes.toString(Charset.defaultCharset())
//        val file = File.createTempFile("sb3", null)
//        file.writeText(fileContent)
//        return file
//    }
//
//    fun s3UrlToBase64() {
//        val url = URL(s3url)
//        val conn = url.openConnection() as HttpURLConnection
//        conn.requestMethod = "GET"
//        val inputStream = conn.inputStream
//        val outputStream = ByteArrayOutputStream()
//        val buffer = ByteArray(1024)
//        var length: Int
//        while (inputStream.read(buffer).also { length = it } != -1) {
//            outputStream.write(buffer, 0, length)
//        }
//        val data = outputStream.toByteArray()
//        datalinksave2 = Base64.encodeToString(data, Base64.DEFAULT)
//
//    }
}