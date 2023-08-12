package org.merakilearn.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.merakilearn.R

class ArduinoBlocklyActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var myRequest: PermissionRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduinoblockly)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient(this)
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.addJavascriptInterface(this, "Arduino")
        webView.loadUrl("https://arduino.merd-bhanwaridevi.merakilearn.org/")
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
    fun onBack() {
        Toast.makeText(this, "Exiting Scratch", Toast.LENGTH_SHORT).show()
        finish()
        onBackPressed()
    }

    class MyWebViewClient internal constructor(private val activity: Activity) : android.webkit.WebViewClient()  {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
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