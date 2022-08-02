package org.merakilearn.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.parcel.Parcelize
import org.merakilearn.R
import org.merakilearn.core.extentions.KEY_ARG
import org.merakilearn.core.extentions.toBundle

@Parcelize
object ScratchActivityArgs: Parcelable
class ScratchActivity : AppCompatActivity() {

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
    private lateinit var webView: WebView
    lateinit var progressBar:ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch)

        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.progressBar)
        setProgressValue(0)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://scratch.merakilearn.org")

        webView.settings.javaScriptEnabled = true

        webView.settings.setSupportZoom(true)


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

    private fun setProgressValue(progress: Int) {

        // set the progress
        progressBar.setProgress(progress)
        // thread is used to change the progress value
        val thread = Thread {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            setProgressValue(progress + 1)
        }
        thread.start()
    }


    override fun onBackPressed() {
        // if your webview can go back it will go back
        if (webView.canGoBack())
            webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }
}

