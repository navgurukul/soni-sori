package org.navgurukul.chat.features.attachmentviewer

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

    open fun onRecycled() {
        boundResourceUid = null
    }

    open fun onAttached() {}
    open fun onDetached() {}
    open fun entersBackground() {}
    open fun entersForeground() {}
    open fun onSelected(selected: Boolean) {}

    open fun handleCommand(commands: AttachmentCommands) {}

    var boundResourceUid: String? = null

    open fun bind(attachmentInfo: AttachmentInfo) {
        boundResourceUid = attachmentInfo.uid
    }
}

class UnsupportedViewHolder constructor(itemView: View) :
        BaseViewHolder(itemView)
