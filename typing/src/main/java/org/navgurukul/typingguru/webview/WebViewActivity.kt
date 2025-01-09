package org.navgurukul.typingguru.webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.merakilearn.databinding.ActivityMainBinding
import org.navgurukul.commonui.platform.BaseActivity
import org.navgurukul.typing.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, WebViewActivity::class.java)
    }

    private val viewModel: WebViewActivityViewModel by viewModel()
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        binding.webview.webViewClient = MyBrowser {
            viewModel.handle(WebViewActivityViewEvents.OnNavigate(it))
        }

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            if (binding.webview.canGoBack()) {
                binding.webview.goBack()
            } else {
                finish()
            }
        }

        viewModel.viewState.observe(this, {
            it?.url?.let { url -> binding.webview.loadUrl(url) }
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
