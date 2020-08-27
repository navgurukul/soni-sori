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
import im.vector.matrix.android.api.session.room.members.ChangeMembershipState
import im.vector.matrix.android.api.session.room.model.RoomSummary
import im.vector.matrix.android.api.util.toMatrixItem
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.extensions.localDateTime
import org.navgurukul.chat.core.resources.DateProvider
import org.navgurukul.chat.core.resources.StringProvider
import org.navgurukul.chat.core.utils.DebouncedClickListener
import org.navgurukul.chat.features.home.room.TypingHelper
import org.navgurukul.chat.features.home.room.format.DisplayableEventFormatter
import javax.inject.Inject

interface Listener {
    fun onRoomClicked(room: RoomSummary)
}

class RoomSummaryItemFactory(
    private val displayableEventFormatter: DisplayableEventFormatter,
    private val dateFormatter: SaralDateFormatter,
//    private val stringProvider: StringProvider,
    private val typingHelper: TypingHelper
) {

    fun create(
        roomSummary: RoomSummary,
        listener: Listener?
    ): RoomSummaryItem {
        return when (roomSummary.membership) {
//            Membership.INVITE -> {
//                val changeMembershipState =
//                    roomChangeMembershipStates[roomSummary.roomId] ?: ChangeMembershipState.Unknown
//                createInvitationItem(roomSummary, changeMembershipState, listener)
//            }
            else -> createRoomItem(
                roomSummary,
                listener?.let { it::onRoomClicked })
        }
    }

//    private fun createInvitationItem(
//        roomSummary: RoomSummary,
//        changeMembershipState: ChangeMembershipState,
//        listener: Listener?
//    ): RoomSummaryItem {
//        val secondLine = if (roomSummary.isDirect) {
//            roomSummary.inviterId
//        } else {
//            roomSummary.inviterId?.let {
//                stringProvider.getString(R.string.invited_by, it)
//            }
//        }
//
//        return RoomSummaryItem(
//            id = roomSummary.roomId,
//            matrixItem = roomSummary.toMatrixItem(),
//            secondLine = secondLine,
//            itemClickListener = View.OnClickListener { listener?.onRoomClicked(roomSummary) })
//    }

    fun createRoomItem(
        roomSummary: RoomSummary,
        onClick: ((RoomSummary) -> Unit)?
    ): RoomSummaryItem {
        val unreadCount = roomSummary.notificationCount
        var latestFormattedEvent: CharSequence = ""
        var latestEventTime: CharSequence = ""
        val latestEvent = roomSummary.latestPreviewableEvent
        if (latestEvent != null) {
            val date = latestEvent.root.localDateTime()
            val currentDate = DateProvider.currentLocalDateTime()
            val isSameDay = date.toLocalDate() == currentDate.toLocalDate()
            latestFormattedEvent =
                displayableEventFormatter.format(latestEvent, roomSummary.isDirect.not())
            latestEventTime = if (isSameDay) {
                dateFormatter.formatMessageHour(date)
            } else {
                dateFormatter.formatMessageDay(date)
            }
        }
        val typingMessage = typingHelper.getTypingMessage(roomSummary.typingUsers)
        return RoomSummaryItem(
            id = roomSummary.roomId,
            matrixItem = roomSummary.toMatrixItem(),
            lastEventTime = latestEventTime,
            typingMessage = typingMessage,
            lastFormattedEvent = latestFormattedEvent,
            unreadNotificationCount = unreadCount,
            hasUnreadMessage = roomSummary.hasUnreadMessages,
            hasDraft = roomSummary.userDrafts.isNotEmpty(),
            itemClickListener =
            DebouncedClickListener(View.OnClickListener {
                onClick?.invoke(roomSummary)
            })
        )
    }
}
