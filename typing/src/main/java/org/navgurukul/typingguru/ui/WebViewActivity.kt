package org.navgurukul.typingguru.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import org.koin.android.ext.android.inject
import org.merakilearn.datasource.Config
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.utils.Utility
import timber.log.Timber

class WebViewActivity : BaseActivity() {
    lateinit var mWebView: WebView
    //injecting Utility class from Koin
    private val utility : Utility by inject()
    private val config: Config by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        mWebView = findViewById<View>(R.id.webview) as WebView
        mWebView.webViewClient = MyBrowser()
        var webUrl: String = config.getValue(Config.KEYBOARD_URL_KEY)
        webUrl = if (utility.isOtgSupported(this)) webUrl+"?otg=true" else webUrl+"?otg=false"
        Timber.d("Key board Url $webUrl")
        mWebView.loadUrl(webUrl)
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

    override fun shouldInstallDynamicModule(): Boolean = true
}