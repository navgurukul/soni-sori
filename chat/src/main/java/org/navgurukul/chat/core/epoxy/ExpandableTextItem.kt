package org.navgurukul.chat.core.epoxy

import android.animation.ObjectAnimator
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.copyOnLongClick

@EpoxyModelClass
abstract class ExpandableTextItem : MerakiEpoxyModel<ExpandableTextItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_expandable_textview

    @EpoxyAttribute
    lateinit var content: String

    @EpoxyAttribute
    var maxLines: Int = 3

    private var isExpanded = false
    private var expandedLines = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.content.text = content
        holder.content.copyOnLongClick()

        holder.content.doOnPreDraw {
            if (holder.content.lineCount > maxLines) {
                expandedLines = holder.content.lineCount
                holder.content.maxLines = maxLines

                holder.view.setOnClickListener {
                    if (isExpanded) {
                        collapse(holder)
                    } else {
                        expand(holder)
                    }
                }
                holder.arrow.isVisible = true
            } else {
                holder.arrow.isVisible = false
            }
        }
    }

    private fun expand(holder: Holder) {
        ObjectAnimator
                .ofInt(holder.content, "maxLines", expandedLines)
                .setDuration(200)
                .start()

        holder.content.ellipsize = null
        holder.arrow.setImageResource(R.drawable.ic_expand_less)
        holder.arrow.contentDescription = holder.view.context.getString(R.string.merged_events_collapse)
        isExpanded = true
    }

    private fun collapse(holder: Holder) {
        ObjectAnimator
                .ofInt(holder.content, "maxLines", maxLines)
                .setDuration(200)
                .start()

        holder.content.ellipsize = TextUtils.TruncateAt.END
        holder.arrow.setImageResource(R.drawable.ic_expand_more)
        holder.arrow.contentDescription = holder.view.context.getString(R.string.merged_events_expand)
        isExpanded = false
    }

    class Holder : MerakiEpoxyHolder() {
        val content by bind<TextView>(R.id.expandableContent)
        val arrow by bind<ImageView>(R.id.expandableArrow)
    }
}
