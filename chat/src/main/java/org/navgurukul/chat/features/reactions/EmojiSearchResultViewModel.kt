package org.navgurukul.chat.features.reactions

import org.navgurukul.chat.features.reactions.data.EmojiDataSource
import org.navgurukul.chat.features.reactions.data.EmojiItem
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.ViewState

data class EmojiSearchResultViewState(
        val query: String = "",
        val results: List<EmojiItem> = emptyList()
) : ViewState

class EmojiSearchResultViewModel(
    initialState: EmojiSearchResultViewState,
    private val dataSource: EmojiDataSource
)
    : BaseViewModel<EmptyViewEvents, EmojiSearchResultViewState>(initialState) {

    fun handle(action: EmojiSearchAction) {
        when (action) {
            is EmojiSearchAction.UpdateQuery -> updateQuery(action)
        }
    }

    private fun updateQuery(action: EmojiSearchAction.UpdateQuery) {
        setState {
            copy(
                    query = action.queryString,
                    results = dataSource.filterWith(action.queryString)
            )
        }
    }
}
