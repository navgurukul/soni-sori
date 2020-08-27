package org.navgurukul.chat.features.home.room.list

import android.view.View
import im.vector.matrix.android.api.util.MatrixItem

class RoomSummaryItem(
    val id: String,
    val typingMessage: CharSequence,
    val matrixItem: MatrixItem,
    val lastFormattedEvent: CharSequence,
    val lastEventTime: CharSequence,
    val unreadNotificationCount: Int = 0,
    val hasUnreadMessage: Boolean = false,
    val hasDraft: Boolean = false,
    val itemClickListener: View.OnClickListener? = null
)