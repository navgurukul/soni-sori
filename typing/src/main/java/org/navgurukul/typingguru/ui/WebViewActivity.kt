package org.navgurukul.typingguru.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.utils.RemoteConfig
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.utils.Utility

class WebViewActivity : AppCompatActivity() {
    private val TAG = "WebViewActivity"
    lateinit var mWebView: WebView
    private val KEYBOARD_URL_KEY = "keyboard_purchase_url"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        mWebView = findViewById<View>(R.id.webview) as WebView
        mWebView.webViewClient = MyBrowser()
        val remoteConfig = TypingGuruPreferenceManager.instance().remoteConfig
        if (remoteConfig != null) {
            val webUrl = remoteConfig.getValueFromRemoteConfig(KEYBOARD_URL_KEY)
            Log.d(TAG, "Web url : $webUrl")
            mWebView.loadUrl(webUrl)
        }
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