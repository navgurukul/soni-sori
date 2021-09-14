package org.navgurukul.typingguru.webview

import org.merakilearn.core.datasource.Config
import org.navgurukul.commonui.platform.*
import org.navgurukul.typingguru.utils.SystemUtils

class WebViewActivityViewModel(systemUtils: SystemUtils, config: Config) :
    BaseViewModel<WebViewActivityViewEvents, WebViewActivityViewState>(WebViewActivityViewState()) {

    companion object {
        const val OTG_KEY = "otg"
    }

    init {
        val webUrl: String = config.getValue<String>(Config.KEYBOARD_URL_KEY).let {
            systemUtils.buildUrl(it, OTG_KEY to systemUtils.isOtgSupported().toString())
        }

        setState {
            copy(url = webUrl)
        }
    }

    fun handle(event: WebViewActivityViewEvents) {
        when (event) {
            is WebViewActivityViewEvents.OnNavigate -> {
                event.url?.let {
                    setState {
                        copy(url = it)
                    }
                }
            }
        }
    }
}

data class WebViewActivityViewState(val url: String? = null) : ViewState

sealed class WebViewActivityViewEvents : ViewEvents {
    data class OnNavigate(val url: String?) : WebViewActivityViewEvents()
}