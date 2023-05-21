package org.merakilearn.core.features.attachmentviewer

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

    open fun handleCommand(commands: org.merakilearn.core.features.attachmentviewer.AttachmentCommands) {}

    var boundResourceUid: String? = null

    open fun bind(attachmentInfo: _root_ide_package_.org.merakilearn.core.features.attachmentviewer.AttachmentInfo) {
        boundResourceUid = attachmentInfo.uid
    }
}

class UnsupportedViewHolder constructor(itemView: View) :
        BaseViewHolder(itemView)
