package org.navgurukul.typingguru.keyboard.dialog

import org.merakilearn.core.navigator.Mode
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.utils.SystemUtils
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager

class KeyboardDialogViewModel(
    private val args: KeyboardDialogArgs,
    systemUtils: SystemUtils,
    stringProvider: StringProvider,
    private val typingManager: TypingGuruPreferenceManager
) :
    BaseViewModel<KeyboardDialogViewEvents, KeyboardDialogViewState>(KeyboardDialogViewState()) {

    init {
        val infoText = stringProvider.getString(
            if (systemUtils.isOtgSupported()) {
                R.string.otg_support
            } else {
                R.string.no_otg_support
            }
        )
        setState { copy(infoText = infoText) }
    }

    fun handle(keyboardDialogViewAction: KeyboardDialogViewActions) {
        when (keyboardDialogViewAction) {
            KeyboardDialogViewActions.BuyButtonClicked -> {
                typingManager.setWebViewDisplayStatus(true)
                _viewEvents.setValue(KeyboardDialogViewEvents.OpenWebViewActivity)
                _viewEvents.setValue(KeyboardDialogViewEvents.Dismiss)
            }
            KeyboardDialogViewActions.OwnButtonClicked -> {
                typingManager.setWebViewDisplayStatus(true)
                _viewEvents.setValue(KeyboardDialogViewEvents.OpenKeyboardActivity(args.mode))
                _viewEvents.setValue(KeyboardDialogViewEvents.Dismiss)
            }
        }
    }
}

data class KeyboardDialogViewState(val infoText: String = "") : ViewState
sealed class KeyboardDialogViewEvents : ViewEvents {
    object Dismiss: KeyboardDialogViewEvents()
    data class OpenKeyboardActivity(val mode: Mode): KeyboardDialogViewEvents()
    object OpenWebViewActivity: KeyboardDialogViewEvents()
}

sealed class KeyboardDialogViewActions: ViewModelAction {
    object OwnButtonClicked: KeyboardDialogViewActions()
    object BuyButtonClicked: KeyboardDialogViewActions()
}
