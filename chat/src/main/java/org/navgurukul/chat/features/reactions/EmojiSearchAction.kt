package org.navgurukul.chat.features.reactions

import org.navgurukul.commonui.platform.ViewModelAction

sealed class EmojiSearchAction : ViewModelAction {
    data class UpdateQuery(val queryString: String) : EmojiSearchAction()
}
