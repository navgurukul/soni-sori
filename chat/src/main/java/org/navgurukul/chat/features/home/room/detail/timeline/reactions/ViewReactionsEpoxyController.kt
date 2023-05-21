package org.navgurukul.chat.features.home.room.detail.timeline.reactions

import com.airbnb.epoxy.TypedEpoxyController
import org.navgurukul.chat.EmojiCompatWrapper
import org.navgurukul.chat.R
import org.navgurukul.chat.core.ui.list.genericFooterItem
import org.navgurukul.chat.core.ui.list.genericLoaderItem
import org.navgurukul.commonui.model.Fail
import org.navgurukul.commonui.model.Incomplete
import org.navgurukul.commonui.model.Success
import org.navgurukul.commonui.resources.StringProvider

/**
 * Epoxy controller for reaction event list
 */
class ViewReactionsEpoxyController(
    private val stringProvider: StringProvider,
    private val emojiCompatWrapper: EmojiCompatWrapper
)
    : TypedEpoxyController<DisplayReactionsViewState>() {

    var listener: Listener? = null

    override fun buildModels(state: DisplayReactionsViewState) {
        when (state.mapReactionKeyToMemberList) {
            is Incomplete -> {
                genericLoaderItem {
                    id("Spinner")
                }
            }
            is Fail -> {
                genericFooterItem {
                    id("failure")
                    text(stringProvider.getString(R.string.unknown_error))
                }
            }
            is Success -> {
                state.mapReactionKeyToMemberList()?.forEach {
                    reactionInfoSimpleItem {
                        id(it.eventId)
                        timeStamp(it.timestamp)
                        reactionKey(emojiCompatWrapper.safeEmojiSpanify(it.reactionKey))
                        authorDisplayName(it.authorName ?: it.authorId)
                        userClicked { listener?.didSelectUser(it.authorId) }
                    }
                }
            }
        }
    }

    interface Listener {
        fun didSelectUser(userId: String)
    }
}
