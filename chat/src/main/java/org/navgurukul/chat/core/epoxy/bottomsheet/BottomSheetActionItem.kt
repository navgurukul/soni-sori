package org.navgurukul.chat.core.epoxy.bottomsheet

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.commonui.themes.ThemeUtils

/**
 * A action for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetActionItem : MerakiEpoxyModel<BottomSheetActionItem.Holder>() {

    @EpoxyAttribute
    @DrawableRes
    var iconRes: Int = 0
    @EpoxyAttribute
    var textRes: Int = 0
    @EpoxyAttribute
    var showExpand = false
    @EpoxyAttribute
    var expanded = false
    @EpoxyAttribute
    var selected = false
    @EpoxyAttribute
    var subMenuItem = false
    @EpoxyAttribute
    var destructive = false
    @EpoxyAttribute
    lateinit var listener: View.OnClickListener

    override fun getDefaultLayout(): Int = R.layout.item_bottom_sheet_action


    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.setOnClickListener {
            listener.onClick(it)
        }
        holder.startSpace.isVisible = subMenuItem
        val tintColor = if (destructive) {
            ThemeUtils.getColor(holder.view.context, R.attr.colorError)
        } else {
            ThemeUtils.getColor(holder.view.context, R.attr.textSecondary)
        }
        holder.icon.setImageResource(iconRes)
        ImageViewCompat.setImageTintList(holder.icon, ColorStateList.valueOf(tintColor))
        holder.text.setText(textRes)
        holder.text.setTextColor(tintColor)
        holder.selected.isInvisible = !selected
        if (showExpand) {
            val expandDrawable = if (expanded) {
                AppCompatResources.getDrawable(holder.view.context, R.drawable.ic_expand_less)
            } else {
                AppCompatResources.getDrawable(holder.view.context, R.drawable.ic_expand_more)
            }
            expandDrawable?.also {
                DrawableCompat.setTint(it, tintColor)
            }
            holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, expandDrawable, null)
        } else {
            holder.text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }

    class Holder : MerakiEpoxyHolder() {
        val startSpace by bind<View>(R.id.actionStartSpace)
        val icon by bind<ImageView>(R.id.actionIcon)
        val text by bind<TextView>(R.id.actionTitle)
        val selected by bind<ImageView>(R.id.actionSelected)
    }
}
