package org.navgurukul.commonui.platform

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(view: View): RecyclerView.ViewHolder(view) {
    open fun onBind(model: T) {}
}