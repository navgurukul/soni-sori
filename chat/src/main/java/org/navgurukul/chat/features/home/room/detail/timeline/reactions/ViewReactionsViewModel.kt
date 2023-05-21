package org.navgurukul.chat.features.home.room.detail.timeline.reactions

import org.matrix.android.sdk.api.session.room.model.ReactionAggregatedSummary
import org.matrix.android.sdk.rx.RxRoom
import org.matrix.android.sdk.rx.unwrap
import io.reactivex.Observable
import io.reactivex.Single
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewEvents
import org.navgurukul.commonui.platform.ViewState

data class DisplayReactionsViewState(
    val eventId: String,
    val roomId: String,
    val mapReactionKeyToMemberList: Async<List<ReactionInfo>> = Uninitialized
) : ViewState

data class ReactionInfo(
    val eventId: String,
    val reactionKey: String,
    val authorId: String,
    val authorName: String? = null,
    val timestamp: String? = null
)

/**
 * Used to display the list of members that reacted to a given event
 */
class ViewReactionsViewModel(
    initialState: DisplayReactionsViewState,
    activeSessionHolder: ActiveSessionHolder,
    private val dateFormatter: SaralDateFormatter
) : BaseViewModel<EmptyViewEvents, DisplayReactionsViewState>(initialState) {

    private val roomId = initialState.roomId
    private val eventId = initialState.eventId
    private val session = activeSessionHolder.getActiveSession()
    private val room = session.getRoom(roomId)
        ?: throw IllegalStateException("Shouldn't use this ViewModel without a room")

    init {
        observeEventAnnotationSummaries()
    }

    private fun observeEventAnnotationSummaries() {
        RxRoom(room)
            .liveAnnotationSummary(eventId)
            .unwrap()
            .flatMapSingle { summaries ->
                Observable
                    .fromIterable(summaries.reactionsSummary)
                    // .filter { reactionAggregatedSummary -> isSingleEmoji(reactionAggregatedSummary.key) }
                    .toReactionInfoList()
            }
            .execute {
                copy(mapReactionKeyToMemberList = it)
            }
    }

    private fun Observable<ReactionAggregatedSummary>.toReactionInfoList(): Single<List<ReactionInfo>> {
        return flatMap { summary ->
            Observable
                .fromIterable(summary.sourceEvents)
                .map {
                    val event = room.getTimeLineEvent(it)
                        ?: throw RuntimeException("Your eventId is not valid")
                    ReactionInfo(
                        event.root.eventId!!,
                        summary.key,
                        event.root.senderId ?: "",
                        event.senderInfo.disambiguatedDisplayName,
                        dateFormatter.formatRelativeDateTime(event.root.originServerTs)

                    )
                }
        }.toList()
    }
}
