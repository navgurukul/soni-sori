package org.navgurukul.chat.features.home.room.list

import androidx.recyclerview.widget.DefaultItemAnimator

private const val ANIM_DURATION_IN_MILLIS = 200L

class RoomListAnimator : DefaultItemAnimator() {

    init {
        addDuration = ANIM_DURATION_IN_MILLIS
        removeDuration = ANIM_DURATION_IN_MILLIS
        moveDuration = 0
        changeDuration = 0
    }
}