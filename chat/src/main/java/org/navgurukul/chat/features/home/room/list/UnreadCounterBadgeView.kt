package org.navgurukul.chat.features.home.room.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import org.navgurukul.chat.R

class UnreadCounterBadgeView : AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun render(state: State) {
        if (state.count == 0) {
            visibility = View.INVISIBLE
        } else {
            visibility = View.VISIBLE
            val bgRes = if (state.highlighted) {
                R.drawable.bg_unread_highlight
            } else {
                R.drawable.bg_unread_notification
            }
            setBackgroundResource(bgRes)
            text = RoomSummaryFormatter.formatUnreadMessagesCounter(state.count)
        }
    }

    data class State(
        val count: Int,
        val highlighted: Boolean
    )
}