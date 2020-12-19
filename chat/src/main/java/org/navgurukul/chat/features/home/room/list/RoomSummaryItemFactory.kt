/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.navgurukul.chat.features.home.room.list

import android.view.View
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.toMatrixItem
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.extensions.localDateTime
import org.navgurukul.chat.core.resources.DateProvider
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.TypingHelper
import org.navgurukul.chat.features.home.room.format.DisplayableEventFormatter

class RoomSummaryItemFactory(
    private val displayableEventFormatter: DisplayableEventFormatter,
    private val dateFormatter: SaralDateFormatter,
    private val avatarRenderer: AvatarRenderer,
    private val typingHelper: TypingHelper
) {

    fun create(
        roomSummary: RoomSummary,
        listener: RoomSummaryController.Listener?
    ): RoomSummaryItem {
        return createRoomItem(
            roomSummary,
            listener?.let { it::onRoomClicked },
            listener?.let { it::onRoomLongClicked })
    }

    fun createRoomItem(
        roomSummary: RoomSummary,
        onClick: ((RoomSummary) -> Unit)?,
        onLongClick: ((RoomSummary) -> Boolean)?
    ): RoomSummaryItem {
        val unreadCount = roomSummary.notificationCount
        val showHighlighted = roomSummary.highlightCount > 0
        var latestFormattedEvent: CharSequence = ""
        var latestEventTime: CharSequence = ""
        val latestEvent = roomSummary.latestPreviewableEvent
        if (latestEvent != null) {
            val date = latestEvent.root.localDateTime()
            val currentDate = DateProvider.currentLocalDateTime()
            val isSameDay = date.toLocalDate() == currentDate.toLocalDate()
            latestFormattedEvent = displayableEventFormatter.format(latestEvent, roomSummary.isDirect.not())
            latestEventTime = if (isSameDay) {
                dateFormatter.formatMessageHour(date)
            } else {
                dateFormatter.formatMessageDay(date)
            }
        }
        val typingMessage = typingHelper.getTypingMessage(roomSummary.typingUsers)
        return RoomSummaryItem_()
            .id(roomSummary.roomId)
            .avatarRenderer(avatarRenderer)
            .matrixItem(roomSummary.toMatrixItem())
            .lastEventTime(latestEventTime)
            .typingMessage(typingMessage)
            .lastEvent(latestFormattedEvent.toString())
            .lastFormattedEvent(latestFormattedEvent)
            .showHighlighted(showHighlighted)
            .unreadNotificationCount(unreadCount)
            .hasUnreadMessage(roomSummary.hasUnreadMessages)
            .hasDraft(roomSummary.userDrafts.isNotEmpty())
            .itemLongClickListener { _ ->
                onLongClick?.invoke(roomSummary) ?: false
            }
            .itemClickListener(
                DebouncedClickListener(View.OnClickListener { _ ->
                    onClick?.invoke(roomSummary)
                })
            )
    }
}
