package org.merakilearn.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.webkit.WebView
import android.webkit.WebViewClient
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scratch)

                webView = findViewById(R.id.webview)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://scratchdev.merakilearn.org/")

        webView.settings.javaScriptEnabled = true

        webView.settings.setSupportZoom(true)
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
