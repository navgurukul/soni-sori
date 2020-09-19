package org.navgurukul.chat.features.home.room.list

import android.view.View
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.facebook.drawee.view.SimpleDraweeView
import org.koin.java.KoinJavaComponent.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.platform.BaseViewHolder

class ChatListItemViewHolder(view: View) : BaseViewHolder<RoomSummaryItem>(view) {

    private val avatarRenderer: AvatarRenderer by inject(AvatarRenderer::class.java)

    private val avatarView = view.findViewById<SimpleDraweeView>(R.id.roomAvatarView)
    private val roomNameView = view.findViewById<TextView>(R.id.roomNameView)
    private val lastEventView = view.findViewById<TextView>(R.id.roomLastEventView)
    private val lastEventTimeView = view.findViewById<TextView>(R.id.roomLastEventTimeView)
    private val typingView = view.findViewById<TextView>(R.id.roomTypingView)

    override fun onBind(model: RoomSummaryItem) {
        avatarRenderer.render(model.matrixItem, avatarView)
        roomNameView.text = model.matrixItem.getBestName()
        lastEventView.text = model.lastFormattedEvent
        lastEventTimeView.text = model.lastEventTime

        typingView.setTextOrHide(model.typingMessage)
        lastEventTimeView.isInvisible = typingView.isVisible

        itemView.setOnClickListener(model.itemClickListener)
    }


}
