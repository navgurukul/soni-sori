package org.merakilearn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import org.merakilearn.R
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle


@Parcelize
object ScratchActivityArgs: Parcelable
class ScratchActivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var progressBar:ProgressBar
        companion object {
        fun start(context: Context) {
            val intent = Intent(context,ScratchActivity::class.java).apply {
                putExtras(ScratchActivityArgs.toBundle()!!)
            }
            context.startActivity(intent)
        }
    }
    private val args: ScratchActivityArgs? by lazy {
        intent.extras?.getParcelable(KEY_ARG)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch)

        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progressBar)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/index.html")

        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.addJavascriptInterface(this, "Scratch")



    }


    @JavascriptInterface
    fun onPageFinished() {

        finish()
        Toast.makeText(applicationContext,"File Saved",Toast.LENGTH_LONG).show()

    }

    @JavascriptInterface
    fun onBack() {
        finish()
        Toast.makeText(applicationContext,"Scratch Exit",Toast.LENGTH_LONG).show()
    }



    // Overriding WebViewClient functions
    inner class WebViewClient : android.webkit.WebViewClient() {
        // Load the URL
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        // ProgressBar will disappear once page is loaded
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
           progressBar.visibility = View.GONE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val context: Context = this
        val pathFilename = context.filesDir.path + "savedWebPage.mht"
        webView.saveWebArchive(pathFilename)
        Toast.makeText(applicationContext,"File Saved on Destroy",Toast.LENGTH_LONG).show()
    }


}


