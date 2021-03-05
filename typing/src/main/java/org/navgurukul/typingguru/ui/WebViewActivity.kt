package org.navgurukul.typingguru.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.navgurukul.typingguru.R

class WebViewActivity : AppCompatActivity() {
    lateinit var mWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        mWebView = findViewById<View>(R.id.webview) as WebView
        mWebView.webViewClient = MyBrowser()
        mWebView.loadUrl("https://navgurukul.github.io/rosa-parks/firstPage.html");
    }

    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if (url != null) {
                view.loadUrl(url)
            }
            return true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.getAction() === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}