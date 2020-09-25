package org.navgurukul.chat.features.home.room.detail.timeline.helper

import im.vector.matrix.android.api.session.room.model.RoomSummary

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