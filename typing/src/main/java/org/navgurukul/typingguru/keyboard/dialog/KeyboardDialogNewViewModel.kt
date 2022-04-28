package org.navgurukul.typingguru.keyboard.dialog

import org.merakilearn.core.navigator.Mode
import org.merakilearn.core.navigator.ModeNew
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents
import org.navgurukul.commonui.platform.ViewModelAction
import org.navgurukul.commonui.platform.ViewState
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.typingguru.R
import org.navgurukul.typingguru.utils.SystemUtils
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager

class KeyboardDialogNewViewModel(
    private val args: KeyboardDialogNewArgs,
    systemUtils: SystemUtils,
    stringProvider: StringProvider,
    private val typingManager: TypingGuruPreferenceManager
) :
    BaseViewModel<KeyboardDialogViewEventsNew, KeyboardDialogViewStateNew>(KeyboardDialogViewStateNew()) {

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
                _viewEvents.setValue(KeyboardDialogViewEventsNew.OpenWebViewActivity)
                _viewEvents.setValue(KeyboardDialogViewEventsNew.Dismiss)
            }
            KeyboardDialogViewActions.OwnButtonClicked -> {
                typingManager.setWebViewDisplayStatus(true)
                _viewEvents.setValue(KeyboardDialogViewEventsNew.OpenKeyboardActivity(args.mode))
                _viewEvents.setValue(KeyboardDialogViewEventsNew.Dismiss)
            }
        }
    }
}

data class KeyboardDialogViewStateNew(val infoText: String = "") : ViewState
sealed class KeyboardDialogViewEventsNew : ViewEvents {
    object Dismiss: KeyboardDialogViewEventsNew()
    data class OpenKeyboardActivity(val mode: ModeNew): KeyboardDialogViewEventsNew()
    object OpenWebViewActivity: KeyboardDialogViewEventsNew()
}

sealed class KeyboardDialogViewActionsNew: ViewModelAction {
    object OwnButtonClicked: KeyboardDialogViewActionsNew()
    object BuyButtonClicked: KeyboardDialogViewActionsNew()
}
