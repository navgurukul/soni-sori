package org.merakilearn.ui

import android.Manifest
import java.util.Base64
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


class ScratchActivity : AppCompatActivity() {
    lateinit var s3link : String
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_scratch)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        scratchRepository = ScratchRepositoryImpl(this)
//        file = intent.extras?.get(Constants.INTENT_EXTRA_KEY_FILE) as File?
        s3link = intent.extras?.getString(Constants.INTENT_EXTRA_KEY_FILE).toString()


        scratchViewModel = ScratchViewModel(this, userRepo)

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

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            if (!loadSavedFileCalled) {
                loadSavedFileCalled = true
                loadSavedFile(file)
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSavedFile(file: File?) {

        if (progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.GONE

        webView.loadUrl("javascript:openLoaderScreen();")
        val base64 = s3LinkToBase64(s3link)

        if (file != null) {
            savedFileName = file.name
            datalinkload = base64
        } else {
            savedFileName = "defaultMerakiScratchFile.sb3"
            datalinkload = Base64.getEncoder().encodeToString(application.assets.open(
                "defaultMerakiScratchFile.sb3").readBytes())
        }


        webView.loadUrl("javascript:loadProjectUsingBase64('" + savedFileName + "','" + datalinkload + "')")

        Handler(Looper.getMainLooper()).postDelayed({
            webView.loadUrl("javascript:closeLoaderScreen();")
        }, 5000)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun s3LinkToBase64(s3Link: String): String {
        // Extract the S3 key from the link by decoding the URL-encoded string
        val key = URLDecoder.decode(s3Link.substringAfterLast("/"),
            StandardCharsets.UTF_8.toString()
        )

        // Encode the key as a Base64 string using the method reference syntax
        val base64Key = Base64.getEncoder().encodeToString(key.toByteArray())

        return base64Key
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
                                Log.d("going here", "On scratchActivity")
                                val fileName = fileName.text.trim().toString()
                                val isSuccess = withContext(Dispatchers.IO){ scratchViewModel.postFile(datalinksave,fileName)
                                }

                                if(isSuccess){
                                    dialog.dismiss()
                                    showCodeSavedDialog()
                                }
                                else{
                                    dialog.dismiss()
                                }

//                                scratchRepository.saveScratchFile(datalinksave,
//                                    fileName.text.trim().toString(),
//                                    false)

                            }
                            savedFile -> {
                                Log.d("going here 2", "On Activity")
                                val fileName = fileName.text.trim().toString()
                                val isSuccess = withContext(Dispatchers.IO){ scratchViewModel.postFile(datalinksave,fileName)
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

}