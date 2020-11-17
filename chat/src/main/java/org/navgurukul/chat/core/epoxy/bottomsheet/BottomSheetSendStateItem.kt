package org.navgurukul.chat.core.epoxy.bottomsheet

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel

/**
 * A send state for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetSendStateItem : MerakiEpoxyModel<BottomSheetSendStateItem.Holder>() {

    @EpoxyAttribute
    var showProgress: Boolean = false
    @EpoxyAttribute
    lateinit var text: CharSequence
    @EpoxyAttribute
    @DrawableRes
    var drawableStart: Int = 0

    override fun getDefaultLayout(): Int = R.layout.item_bottom_sheet_message_status

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.progress.isVisible = showProgress
        holder.text.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0)
        holder.text.text = text
    }

    class Holder : MerakiEpoxyHolder() {
        val progress by bind<View>(R.id.messageStatusProgress)
        val text by bind<TextView>(R.id.messageStatusText)
    }
}
