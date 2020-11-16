package org.navgurukul.chat.core.ui.list

import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.commonui.themes.ThemeUtils

/**
 * A generic list item.
 * Displays an item with a title, and optional description.
 * Can display an accessory on the right, that can be an image or an indeterminate progress.
 * If provided with an action, will display a button at the bottom of the list item.
 */
@EpoxyModelClass
abstract class GenericFooterItem : MerakiEpoxyModel<GenericFooterItem.Holder>() {

    @EpoxyAttribute
    var text: String? = null

    @EpoxyAttribute
    var style: GenericItem.STYLE = GenericItem.STYLE.NORMAL_TEXT

    @EpoxyAttribute
    var itemClickAction: GenericItem.Action? = null

    @EpoxyAttribute
    var centered: Boolean = true

    @EpoxyAttribute
    @ColorInt
    var textColor: Int? = null

    override fun getDefaultLayout(): Int = R.layout.item_generic_footer

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.text.setTextOrHide(text)
        when (style) {
            GenericItem.STYLE.BIG_TEXT    -> holder.text.textSize = 18f
            GenericItem.STYLE.NORMAL_TEXT -> holder.text.textSize = 14f
        }
        holder.text.gravity = if (centered) Gravity.CENTER_HORIZONTAL else Gravity.START

        if (textColor != null) {
            holder.text.setTextColor(textColor!!)
        } else {
            holder.text.setTextColor(ThemeUtils.getColor(holder.view.context, R.attr.textSecondary))
        }

        holder.view.setOnClickListener {
            itemClickAction?.perform?.run()
        }
    }

    class Holder : MerakiEpoxyHolder() {
        val text by bind<TextView>(R.id.itemGenericFooterText)
    }
}
