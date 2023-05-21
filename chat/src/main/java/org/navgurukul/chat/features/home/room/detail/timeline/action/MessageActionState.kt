package org.navgurukul.chat.features.home.room.detail.timeline.action

import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.core.extensions.canReact
import org.navgurukul.chat.features.home.room.detail.timeline.item.MessageInformationData
import org.navgurukul.commonui.model.Async
import org.navgurukul.commonui.model.Uninitialized
import org.navgurukul.commonui.platform.ViewState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Quick reactions state
 */
data class ToggleState(
        val reaction: String,
        val isSelected: Boolean
)

data class ActionPermissions(
        val canSendMessage: Boolean = false,
        val canReact: Boolean = false,
        val canRedact: Boolean = false
)

data class MessageActionState(
    val roomId: String,
    val eventId: String,
    val informationData: MessageInformationData,
    val timelineEvent: Async<TimelineEvent> = Uninitialized,
    val messageBody: CharSequence = "",
        // For quick reactions
    val quickStates: Async<List<ToggleState>> = Uninitialized,
        // For actions
    val actions: List<EventSharedAction> = emptyList(),
    val expendedReportContentMenu: Boolean = false,
    val actionPermissions: ActionPermissions = ActionPermissions()
) : ViewState {

    private val dateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault())

    fun senderName(): String = informationData.memberName?.toString() ?: ""

    fun time(): String? = timelineEvent()?.root?.originServerTs?.let { dateFormat.format(Date(it)) } ?: ""

    fun canReact() = timelineEvent()?.canReact() == true && actionPermissions.canReact
}
