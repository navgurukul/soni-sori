package org.navgurukul.chat.features.reactions

import android.graphics.Typeface
import android.widget.TextView
import com.airbnb.epoxy.*
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.reactions.data.EmojiItem

@EpoxyModelClass
abstract class EmojiSearchResultItem : EpoxyModelWithHolder<EmojiSearchResultItem.Holder>() {

    @EpoxyAttribute
    lateinit var emojiItem: EmojiItem

    @EpoxyAttribute
    var currentQuery: String? = null

    @EpoxyAttribute
    var onClickListener: ReactionClickListener? = null

    @EpoxyAttribute
    var emojiTypeFace: Typeface? = null

    override fun getDefaultLayout(): Int = R.layout.item_emoji_result

    override fun bind(holder: Holder) {
        super.bind(holder)
        // TODO use query string to highlight the matched query in name and keywords?
        holder.emojiText.text = emojiItem.emoji
        holder.emojiText.typeface = emojiTypeFace ?: Typeface.DEFAULT
        holder.emojiNameText.text = emojiItem.name
        holder.emojiKeywordText.setTextOrHide(emojiItem.keywords.joinToString())
        holder.view.setOnClickListener {
            onClickListener?.onReactionSelected(emojiItem.emoji)
        }
    }

    class Holder : MerakiEpoxyHolder() {
        val emojiText by bind<TextView>(R.id.item_emoji_tv)
        val emojiNameText by bind<TextView>(R.id.item_emoji_name)
        val emojiKeywordText by bind<TextView>(R.id.item_emoji_keyword)
    }
}
