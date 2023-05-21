package org.navgurukul.chat.features.home.room.detail.timeline.helper

import org.matrix.android.sdk.api.session.room.model.RoomSummary

class RoomSummaryHolder {

    var roomSummary: RoomSummary? = null
        private set

    fun set(roomSummary: RoomSummary) {
        this.roomSummary = roomSummary
    }

    fun clear() {
        roomSummary = null
    }
}