package org.merakilearn.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.merakilearn.R
import org.merakilearn.arduinohexupload.ArduinoHexUploadActivity


class ArduinoBlocklyActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var myRequest: PermissionRequest
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduinoblockly)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient(this)
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.addJavascriptInterface(this, "AndroidBridge")
        webView.loadUrl("https://arduino.merd-bhanwaridevi.merakilearn.org/blockly-home")
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


    @JavascriptInterface
    fun hexDataUploadToAndroidDevice(hexData: String) {
        val builder = AlertDialog.Builder(this)
        if( hexData.isNotEmpty() ) {

            editor = sharedPreferences.edit()
            // If data coming as json string

            Log.d(
                "ArduinoBlockly",
                "Read Data from web ${hexData}"
            )

            println(hexData.length)
            editor.putString("HexDataFromSketch1", hexData )
            editor.apply()
            val readHexDataPref = sharedPreferences.getString("HexDataFromSketch1", null)
            if (readHexDataPref.toString().isNotEmpty()) {
                val intent = Intent(this@ArduinoBlocklyActivity, ArduinoHexUploadActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                val bundle = Bundle()
                bundle.putString("HexDataFromSketch1", readHexDataPref)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            // Set the dialog title and message
            builder.setTitle("Failed to Save data in InMemory")
                .setMessage("InMemory HexData is Null")

            // Set positive button and its click listener
            builder.setPositiveButton("OK") { dialog, which ->
                Toast.makeText(this, "Retry to upload the code in mins ", Toast.LENGTH_SHORT).show();
                dialog.dismiss() // Dismiss the dialog
            }
        }
        else {
            // Set the dialog title and message if reading data from web to android fails
            builder.setTitle("Failed to Read Data From API")
                .setMessage("Sketch Code to Hex file data is empty ")

            // Set positive button and its click listener
            builder.setPositiveButton("OK") { dialog, which ->
                Toast.makeText(this, "Retry in sometime", Toast.LENGTH_SHORT).show();
                dialog.dismiss() // Dismiss the dialog
            }
        }
    }

    @JavascriptInterface
    fun onBack() {
        Toast.makeText(this, "Exiting Arduino", Toast.LENGTH_SHORT).show()
        finish()
        onBackPressed()
    }

    inner class MyWebViewClient internal constructor(private val activity: Activity) : android.webkit.WebViewClient()  {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (progressBar.visibility == View.VISIBLE)
                progressBar.visibility = View.GONE
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
            /*Log.d("Scratch", "${consoleMessage.message()} -- From line " +
              "${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")*/
            return true
        }

        override fun onPermissionRequest(request: PermissionRequest) {
            myRequest = request
            for (permission in request.resources) {
                when (permission) {
                    "android.webkit.resource.AUDIO_CAPTURE" -> {
                        askForPermission(
                            request.origin.toString(),
                            Manifest.permission.RECORD_AUDIO,
                            101
                        )
                    }
                    "android.webkit.resource.VIDEO_CAPTURE" -> {
                        askForPermission(
                            request.origin.toString(),
                            Manifest.permission.CAMERA,
                            102
                        )
                    }
                }
            }
        }
    }
}