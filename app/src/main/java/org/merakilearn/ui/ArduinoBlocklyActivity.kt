package org.merakilearn.ui

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.merakilearn.R
import org.merakilearn.arduinohexupload.ArduinoHexUploadActivity
import org.merakilearn.arduinohexupload.UsbSerialManager
import timber.log.Timber


class ArduinoBlocklyActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar: ProgressBar
    lateinit var myRequest: PermissionRequest
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val usbSerialManager: UsbSerialManager? = null

    enum class UsbConnectState {
        DISCONNECTED, CONNECT
    }


    private val mUsbNotifyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbSerialManager.ACTION_USB_PERMISSION_GRANTED -> Toast.makeText(
                    context,
                    "USB permission granted",
                    Toast.LENGTH_SHORT
                ).show()
                UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED -> Toast.makeText(
                    context,
                    "USB Permission denied",
                    Toast.LENGTH_SHORT
                ).show()
                UsbSerialManager.ACTION_NO_USB -> Toast.makeText(
                    context,
                    "No USB connected",
                    Toast.LENGTH_SHORT
                ).show()
                UsbSerialManager.ACTION_USB_DISCONNECTED -> {
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show()
                    usbConnectChange(UsbConnectState.DISCONNECTED)
                }
                UsbSerialManager.ACTION_USB_CONNECT -> {
                    Toast.makeText(context, "USB connected", Toast.LENGTH_SHORT).show()
                    usbConnectChange(UsbConnectState.CONNECT)
                }
                UsbSerialManager.ACTION_USB_NOT_SUPPORTED -> Toast.makeText(
                    context,
                    "USB device not supported",
                    Toast.LENGTH_SHORT
                ).show()
                UsbSerialManager.ACTION_USB_READY -> Toast.makeText(
                    context,
                    "Usb device ready",
                    Toast.LENGTH_SHORT
                ).show()
                UsbSerialManager.ACTION_USB_DEVICE_NOT_WORKING -> Toast.makeText(
                    context,
                    "USB device not working",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val mUsbHardwareReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == UsbSerialManager.ACTION_USB_PERMISSION_REQUEST) {
                val granted = intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
                if (granted) // User accepted our USB connection. Try to open the device as a serial port
                {
                    val grantedDevice =
                        intent.extras!!.getParcelable<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    usbPermissionGranted(grantedDevice!!.deviceName)
                    val it = Intent(UsbSerialManager.ACTION_USB_PERMISSION_GRANTED)
                    context.sendBroadcast(it)
                } else  // User not accepted our USB connection. Send an Intent to the Main Activity
                {
                    val it = Intent(UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED)
                    context.sendBroadcast(it)
                }
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                val it = Intent(UsbSerialManager.ACTION_USB_CONNECT)
                context.sendBroadcast(it)
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                // Usb device was disconnected. send an intent to the Main Activity
                val it = Intent(UsbSerialManager.ACTION_USB_DISCONNECTED)
                context.sendBroadcast(it)
            }
        }
    }

    private fun setUsbFilter() {
        val filter = IntentFilter()
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_REQUEST)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(mUsbHardwareReceiver, filter)
    }

    fun usbConnectChange(state: UsbConnectState) {
        if (state == UsbConnectState.DISCONNECTED) {
//            if (requestButton != null) requestButton.setVisibility(View.INVISIBLE)
//            if (fab != null) fab.hide()
        } else if (state == UsbConnectState.CONNECT) {
//            if (requestButton != null) requestButton.setVisibility(View.VISIBLE)
        }
    }

    fun usbPermissionGranted(usbKey: String) {
        Toast.makeText(this, "UsbPermissionGranted:$usbKey", Toast.LENGTH_SHORT).show()
//        portSelect.setText(usbKey)
//        deviceKeyName = usbKey
//        if (fab != null) fab.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arduinoblockly)

        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE

        setUsbFilter()
        sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

        webView = findViewById(R.id.webView)
        webView.webViewClient = MyWebViewClient(this)
        webView.webChromeClient = WebChromeClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.allowFileAccess = true
        //not allowed in production due to security reasons, so need to check if it works
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.settings.allowFileAccessFromFileURLs = true
        webView.addJavascriptInterface(this, "AndroidBridge")
        webView.loadUrl("https://arduino.merd-bhanwaridevi.merakilearn.org/blockly-home")
    }


    private fun setFilters() {
        val filter = IntentFilter()
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_GRANTED)
        filter.addAction(UsbSerialManager.ACTION_NO_USB)
        filter.addAction(UsbSerialManager.ACTION_USB_DISCONNECTED)
        filter.addAction(UsbSerialManager.ACTION_USB_CONNECT)
        filter.addAction(UsbSerialManager.ACTION_USB_NOT_SUPPORTED)
        filter.addAction(UsbSerialManager.ACTION_USB_PERMISSION_NOT_GRANTED)
        registerReceiver(mUsbNotifyReceiver, filter)
    }

    /* public UsbSerialDevice getUsbSerialDevice(String key) {
        return usbSerialManager.tryGetDevice(key);
    }*/
    override fun onResume() {
        super.onResume()
        setFilters()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mUsbNotifyReceiver)
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

            Timber.tag("ArduinoBlockly").d("Read Data from web " + hexData)

            println(hexData.length)
            editor.putString("HexDataFromSketch1", hexData)
            editor.apply()
            val readHexDataPref = sharedPreferences.getString("HexDataFromSketch1", null)
            if (readHexDataPref.toString().isNotEmpty()) {
                Log.d("HexDataFromSketch1", "value fo hexData ${readHexDataPref.toString()}")
                Log.d("HexDataFromSketch1", "value fo HEXDATAFRom ${readHexDataPref.toString()}")
                val intent =
                    Intent(this@ArduinoBlocklyActivity, ArduinoHexUploadActivity::class.java)
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
                Toast.makeText(this, "Retry to upload the code in mins ", Toast.LENGTH_SHORT)
                    .show();
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
            Log.d("ARDUINO_WEB", "Got Error message! $error , request url: - ${request.url}, request: isForMain ${request.isForMainFrame}  printing request: $request")
            Log.d("ARDUINO_WEB", "Got Error messagecode! ${error.errorCode} , description: ${error.description} ${view.url}")

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
            if (consoleMessage != null) {
                Log.d("ARDUINO_WEB", "${consoleMessage.message()} -- From line " +
                        "${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
            }
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