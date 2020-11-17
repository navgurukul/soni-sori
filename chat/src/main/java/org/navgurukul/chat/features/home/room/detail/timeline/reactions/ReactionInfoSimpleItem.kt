package org.navgurukul.chat.features.home.room.detail.timeline.reactions

import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder

/**
 * Item displaying an emoji reaction (single line with emoji, author, time)
 */
@EpoxyModelClass
abstract class ReactionInfoSimpleItem : EpoxyModelWithHolder<ReactionInfoSimpleItem.Holder>() {

    @EpoxyAttribute
    lateinit var reactionKey: CharSequence
    @EpoxyAttribute
    lateinit var authorDisplayName: CharSequence
    @EpoxyAttribute
    var timeStamp: CharSequence? = null
    @EpoxyAttribute
    var userClicked: (() -> Unit)? = null

    override fun getDefaultLayout(): Int = R.layout.item_simple_reaction_info

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.emojiReactionView.text = reactionKey
        holder.displayNameView.text = authorDisplayName
        timeStamp?.let {
            holder.timeStampView.text = it
            holder.timeStampView.isVisible = true
        } ?: run {
            holder.timeStampView.isVisible = false
        }
        holder.view.setOnClickListener { userClicked?.invoke() }
    }

    class Holder : MerakiEpoxyHolder() {
        val emojiReactionView by bind<TextView>(R.id.itemSimpleReactionInfoKey)
        val displayNameView by bind<TextView>(R.id.itemSimpleReactionInfoMemberName)
        val timeStampView by bind<TextView>(R.id.itemSimpleReactionInfoTime)
    }
}
