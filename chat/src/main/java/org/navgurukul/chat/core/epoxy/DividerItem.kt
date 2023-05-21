package org.navgurukul.chat.core.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R

/**
 * Default background color is for the bottom sheets (R.attr.vctr_list_bottom_sheet_divider_color).
 * To use in fragment, set color using R.attr.riotx_list_divider_color
 */
@EpoxyModelClass
abstract class DividerItem : MerakiEpoxyModel<DividerItem.Holder>() {

    @EpoxyAttribute
    var color: Int = -1

    override fun getDefaultLayout(): Int = R.layout.item_divider

    override fun bind(holder: Holder) {
        super.bind(holder)
        if (color != -1) {
            holder.view.setBackgroundColor(color)
        }
    }

    class Holder : MerakiEpoxyHolder()
}