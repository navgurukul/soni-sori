package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.view.View
import android.view.ViewStub
import android.widget.RelativeLayout
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.utils.DimensionConverter
import org.navgurukul.commonui.platform.CheckableView

/**
 * Children must override getViewType()
 */
abstract class BaseEventItem<H : BaseEventItem.BaseHolder> : MerakiEpoxyModel<H>() {

    // To use for instance when opening a permalink with an eventId
    @EpoxyAttribute
    var highlighted: Boolean = false

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var dimensionConverter: DimensionConverter

    @CallSuper
    override fun bind(holder: H) {
        super.bind(holder)
//        holder.checkableBackground.isChecked = highlighted
    }

    /**
     * Returns the eventIds associated with the EventItem.
     * Will generally get only one, but it handles the merging items.
     */
    abstract fun getEventIds(): List<String>

    abstract class BaseHolder(@IdRes val stubId: Int) : MerakiEpoxyHolder() {
//        val checkableBackground by bind<CheckableView>(R.id.messageSelectedBackground)
//        val readReceiptsView by bind<ReadReceiptsView>(R.id.readReceiptsView)

        override fun bindView(itemView: View) {
            super.bindView(itemView)
            if (stubId != 0) {
                inflateStub()
            }
        }

        private fun inflateStub() {
            view.findViewById<ViewStub>(stubId).inflate()
        }
    }
}