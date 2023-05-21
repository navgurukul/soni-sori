package org.navgurukul.chat.features.home.room.list

import org.matrix.android.sdk.api.session.room.model.RoomSummary

class ChronologicalRoomComparator : Comparator<RoomSummary> {

    override fun compare(leftRoomSummary: RoomSummary?, rightRoomSummary: RoomSummary?): Int {
        return when {
            rightRoomSummary?.latestPreviewableEvent?.root == null -> -1
            leftRoomSummary?.latestPreviewableEvent?.root == null  -> 1
            else                                                   -> {
                val rightTimestamp = rightRoomSummary.latestPreviewableEvent?.root?.originServerTs ?: 0
                val leftTimestamp = leftRoomSummary.latestPreviewableEvent?.root?.originServerTs ?: 0

                val deltaTimestamp = rightTimestamp - leftTimestamp

                when {
                    deltaTimestamp > 0 -> 1
                    deltaTimestamp < 0 -> -1
                    else               -> 0
                }
            }
        }
    }
}
