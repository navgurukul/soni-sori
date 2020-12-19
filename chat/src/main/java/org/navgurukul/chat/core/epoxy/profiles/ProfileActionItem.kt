package org.navgurukul.chat.core.epoxy.profiles

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.themes.ThemeUtils

@EpoxyModelClass
abstract class ProfileActionItem : MerakiEpoxyModel<ProfileActionItem.Holder>() {

    override fun getDefaultLayout(): Int  = R.layout.item_profile_action

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    var subtitle: String? = null

    @EpoxyAttribute
    var iconRes: Int = 0

    @EpoxyAttribute
    var tintIcon: Boolean = true

    @EpoxyAttribute
    var editableRes: Int = R.drawable.ic_arrow_right

    @EpoxyAttribute
    var accessoryRes: Int = 0

    @EpoxyAttribute
    var accessoryMatrixItem: MatrixItem? = null

    @EpoxyAttribute
    var avatarRenderer: AvatarRenderer? = null

    @EpoxyAttribute
    var editable: Boolean = true

    @EpoxyAttribute
    var destructive: Boolean = false

    @EpoxyAttribute
    var listener: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.setOnClickListener(listener)
        if (listener == null) {
            holder.view.isClickable = false
        }
        holder.title.text = title
        val titleTintColor = if (destructive) {
            ThemeUtils.getColor(holder.view.context, R.attr.colorError)
        } else {
            ThemeUtils.getColor(holder.view.context, R.attr.textPrimary)
        }
        val iconTintColor = if (destructive) {
            ThemeUtils.getColor(holder.view.context, R.attr.colorError)
        } else {
            ThemeUtils.getColor(holder.view.context, R.attr.textSecondary)
        }
        holder.title.setTextColor(titleTintColor)
        holder.subtitle.setTextOrHide(subtitle)
        if (iconRes != 0) {
            holder.icon.setImageResource(iconRes)
            if (tintIcon) {
                ImageViewCompat.setImageTintList(holder.icon, ColorStateList.valueOf(iconTintColor))
            } else {
                ImageViewCompat.setImageTintList(holder.icon, null)
            }
            holder.icon.isVisible = true
        } else {
            holder.icon.isVisible = false
        }

        if (accessoryRes != 0) {
            holder.secondaryAccessory.setImageResource(accessoryRes)
            holder.secondaryAccessory.isVisible = true
        } else {
            holder.secondaryAccessory.isVisible = false
        }

        if (accessoryMatrixItem != null) {
            avatarRenderer?.render(accessoryMatrixItem!!, holder.secondaryAccessory)
            holder.secondaryAccessory.isVisible = true
        } else {
            holder.secondaryAccessory.isVisible = false
        }

        if (editableRes != 0 && editable) {
            val tintColorSecondary = if (destructive) {
                titleTintColor
            } else {
                ThemeUtils.getColor(holder.view.context, R.attr.textSecondary)
            }
            holder.editable.setImageResource(editableRes)
            ImageViewCompat.setImageTintList(holder.editable, ColorStateList.valueOf(tintColorSecondary))
            holder.editable.isVisible = true
        } else {
            holder.editable.isVisible = false
        }
    }

    class Holder : MerakiEpoxyHolder() {
        val icon by bind<ImageView>(R.id.actionIcon)
        val title by bind<TextView>(R.id.actionTitle)
        val subtitle by bind<TextView>(R.id.actionSubtitle)
        val editable by bind<ImageView>(R.id.actionEditable)
        val secondaryAccessory by bind<ImageView>(R.id.actionSecondaryAccessory)
    }
}
