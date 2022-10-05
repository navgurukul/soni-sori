package org.navgurukul.typingguru.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typingguru.R

class WebViewActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, WebViewActivity::class.java)
    }

    private val viewModel: WebViewActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webview.webViewClient = MyBrowser {
            viewModel.handle(WebViewActivityViewEvents.OnNavigate(it))
        }

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            if (webview.canGoBack()) {
                webview.goBack()
            } else {
                finish()
            }
        }

        viewModel.viewState.observe(this, {
            it?.url?.let { url -> webview.loadUrl(url) }
        })
    }

    private class MyBrowser(val listener: (String?) -> Unit) : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            listener(url)
            return true
        }
    }

    override fun shouldInstallDynamicModule(): Boolean = true
}