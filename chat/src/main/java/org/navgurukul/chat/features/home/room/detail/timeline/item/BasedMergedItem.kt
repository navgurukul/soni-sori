
package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController

abstract class BasedMergedItem<H : BasedMergedItem.Holder> : BaseEventItem<H>() {

    abstract val attributes: Attributes

    override fun bind(holder: H) {
        super.bind(holder)
        holder.expandView.setOnClickListener {
            attributes.onCollapsedStateChanged(!attributes.isCollapsed)
        }
        if (attributes.isCollapsed) {
            holder.separatorView.visibility = View.GONE
            holder.expandView.setText(R.string.merged_events_expand)
        } else {
            holder.separatorView.visibility = View.VISIBLE
            holder.expandView.setText(R.string.merged_events_collapse)
        }
        // No read receipt for this item
//        holder.readReceiptsView.isVisible = false
    }

    protected val distinctMergeData by lazy {
        attributes.mergeData.distinctBy { it.userId }
    }

    override fun getEventIds(): List<String> {
        return if (attributes.isCollapsed) {
            attributes.mergeData.map { it.eventId }
        } else {
            emptyList()
        }
    }

    data class Data(
        val localId: Long,
        val eventId: String,
        val userId: String,
        val memberName: String,
        val avatarUrl: String?
    )

    fun Data.toMatrixItem() = MatrixItem.UserItem(userId, memberName, avatarUrl)

    interface Attributes {
        val isCollapsed: Boolean
        val mergeData: List<Data>
        val avatarRenderer: AvatarRenderer
        val onCollapsedStateChanged: (Boolean) -> Unit
    }

    abstract class Holder(@IdRes stubId: Int) : BaseEventItem.BaseHolder(stubId) {
        val expandView by bind<TextView>(R.id.itemMergedExpandTextView)
        val separatorView by bind<View>(R.id.itemMergedSeparatorView)
    }
}

