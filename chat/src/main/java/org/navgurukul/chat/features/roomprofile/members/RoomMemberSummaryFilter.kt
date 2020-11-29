package org.navgurukul.chat.features.roomprofile.members

import im.vector.matrix.android.api.extensions.orFalse
import im.vector.matrix.android.api.session.room.model.RoomMemberSummary
import io.reactivex.functions.Predicate

class RoomMemberSummaryFilter: Predicate<RoomMemberSummary> {
    var filter: String = ""

    override fun test(roomMemberSummary: RoomMemberSummary): Boolean {
        if (filter.isEmpty()) {
            // No filter
            return true
        }

        return roomMemberSummary.displayName?.contains(filter, ignoreCase = true).orFalse()
                // We should maybe exclude the domain from the userId
                || roomMemberSummary.userId.contains(filter, ignoreCase = true)
    }
}
