package org.merakilearn.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Base64
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
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

    lateinit var myRequest: PermissionRequest
    var file: File? = null
    var s3Url: String = ""
    var savedFile: Boolean = false
    var savedFileName: String = ""
    var loadSavedFileCalled: Boolean = false
    var datalinkload: String = ""
    var datalinksave: String = ""
    private var fileChooserResultLauncher = createFileChooserResultLauncher()
    private var fileChooserValueCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scratch)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        scratchRepository = ScratchRepositoryImpl(this)
        file = intent.extras?.get(Constants.INTENT_EXTRA_KEY_FILE) as File?

        if(!intent.getStringExtra("s3Url").isNullOrEmpty()){
            s3Url = intent.getStringExtra("s3Url").toString()
        }

        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("file:///android_asset/index.html")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.addJavascriptInterface(this, "Scratch")
        webView.settings.domStorageEnabled = true;
        webView.settings.setAppCacheEnabled(true);
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
    }

    @JavascriptInterface
    fun onSave(globalBase64String: String) {
        datalinksave = globalBase64String
        showCodeSaveDialog()
    }

    @JavascriptInterface
    fun showAlertOnExit(globalBase64String: String) {
        datalinksave = globalBase64String
        showCodeSaveDialog2()
    }

    @JavascriptInterface
    fun onBack() {
        onBackPressedDialog()
    }

    @JavascriptInterface
    fun returnFile(): String{
        return datalinkload
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
            102 -> {
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
            if (consoleMessage != null) {
                Log.d("Scratch", "${consoleMessage.message()} -- From line " +
                        "${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
            }
            return true
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            try {
                fileChooserValueCallback = filePathCallback;
                fileChooserResultLauncher.launch(fileChooserParams?.createIntent())
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this@ScratchActivity,
                    "Error opening file chooser: $e",
                    Toast.LENGTH_LONG
                ).show()
            }
            return true
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
                    "android.webkit.resource.VIDEO_CAPTURE" -> {
                        askForPermission(request.origin.toString(),
                            Manifest.permission.CAMERA,
                            102)
                    }
                }
            }
        }
    }

    private fun createFileChooserResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                fileChooserValueCallback?.onReceiveValue(arrayOf(Uri.parse(it?.data?.dataString)));
            } else {
                fileChooserValueCallback?.onReceiveValue(null)
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

            if (!loadSavedFileCalled && s3Url.isEmpty()) {
                loadSavedFileCalled = true
                loadSavedFile(file)
            } else if (!loadSavedFileCalled && s3Url.isNotEmpty()) {
                loadSavedFileCalled = true
                loadS3Url(s3Url)
            }
        }

    }

    fun loadS3Url(s3Url: String){

        if (progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.GONE

        webView.loadUrl("javascript:openLoaderScreen();")
        webView.loadUrl("javascript:loadProjectUsingS3Url('" + s3Url + "');")

        Handler(Looper.getMainLooper()).postDelayed({
            webView.loadUrl("javascript:closeLoaderScreen();")
        }, 5000)

    }

    fun loadSavedFile(file: File?) {

        if (progressBar.visibility == View.VISIBLE)
            progressBar.visibility = View.GONE

        webView.loadUrl("javascript:openLoaderScreen();")

        if (file != null) {
            savedFileName = file.name
            datalinkload = Base64.encodeToString(file.readBytes(), 2)
        } else {
            savedFileName = "defaultFile.sb3"
            datalinkload = Base64.encodeToString(application.assets.open(
                "defaultFile.sb3").readBytes(), 2)
        }

        webView.loadUrl("javascript:loadProjectUsingFileName('" + savedFileName + "')")

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

    private fun showCodeSaveDialog2() {
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
            onBackPressed()
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

    private fun onBackPressedDialog() {
        runOnUiThread {
            val alertDialog = AlertDialog.Builder(this).apply {
                setTitle("Exit")
                setMessage("Are you sure you want to exit?")
                setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    super.onBackPressed()
                }
                setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
            }

            alertDialog.show()
        }
    }
}
