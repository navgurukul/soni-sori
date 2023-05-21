package org.navgurukul.chat.core.epoxy.profiles

import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyController
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.core.epoxy.ClickListener
import org.navgurukul.chat.core.epoxy.dividerItem
import org.navgurukul.chat.features.home.AvatarRenderer

fun EpoxyController.buildProfileSection(title: String) {
    profileSectionItem {
        id("section_$title")
        title(title)
    }
}

fun EpoxyController.buildProfileAction(
    id: String,
    title: String,
    dividerColor: Int,
    subtitle: String? = null,
    editable: Boolean = true,
    @DrawableRes icon: Int = 0,
    tintIcon: Boolean = true,
    @DrawableRes editableRes: Int? = null,
    destructive: Boolean = false,
    divider: Boolean = true,
    action: ClickListener? = null,
    @DrawableRes accessory: Int = 0,
    accessoryMatrixItem: MatrixItem? = null,
    avatarRenderer: AvatarRenderer? = null
) {
    profileActionItem {
        iconRes(icon)
        tintIcon(tintIcon)
        id("action_$id")
        subtitle(subtitle)
        editable(editable)
        editableRes?.let { editableRes(editableRes) }
        destructive(destructive)
        title(title)
        accessoryRes(accessory)
        accessoryMatrixItem(accessoryMatrixItem)
        avatarRenderer(avatarRenderer)
        listener { _ ->
            action?.invoke()
        }
    }

    if (divider) {
        dividerItem {
            id("divider_$title")
            color(dividerColor)
        }
    }
}
