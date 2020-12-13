package org.navgurukul.chat.core.epoxy.bottomsheet

import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.ClickListener
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.epoxy.onClick
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.resources.StringProvider
import org.navgurukul.commonui.themes.ThemeUtils

/**
 * A room preview for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetRoomPreviewItem : MerakiEpoxyModel<BottomSheetRoomPreviewItem.Holder>() {

    @EpoxyAttribute lateinit var avatarRenderer: AvatarRenderer
    @EpoxyAttribute lateinit var matrixItem: MatrixItem
    @EpoxyAttribute lateinit var stringProvider: StringProvider
    @EpoxyAttribute var izFavorite: Boolean = false
    @EpoxyAttribute var settingsClickListener: ClickListener? = null
    @EpoxyAttribute var favoriteClickListener: ClickListener? = null

    override fun getDefaultLayout(): Int = R.layout.item_bottom_sheet_room_preview

    override fun bind(holder: Holder) {
        super.bind(holder)
        avatarRenderer.render(matrixItem, holder.avatar)
        holder.avatar.onClick(settingsClickListener)
        holder.roomName.setTextOrHide(matrixItem.displayName)
        setFavoriteState(holder, izFavorite)

        holder.roomFavorite.setOnClickListener {
            // Immediate echo
            setFavoriteState(holder, !izFavorite)
            // And do the action
            favoriteClickListener?.invoke()
        }
        holder.roomSettings.onClick(settingsClickListener)
    }

    private fun setFavoriteState(holder: Holder, isFavorite: Boolean) {
        val tintColor: Int
        if (isFavorite) {
            holder.roomFavorite.contentDescription = stringProvider.getString(R.string.room_list_quick_actions_favorite_remove)
            holder.roomFavorite.setImageResource(R.drawable.ic_star_24dp)
            tintColor = ThemeUtils.getColor(holder.view.context, R.attr.colorAccent)
        } else {
            holder.roomFavorite.contentDescription = stringProvider.getString(R.string.room_list_quick_actions_favorite_add)
            holder.roomFavorite.setImageResource(R.drawable.ic_star_24dp)
            tintColor = ThemeUtils.getColor(holder.view.context, R.attr.textSecondary)
        }
        ImageViewCompat.setImageTintList(holder.roomFavorite, ColorStateList.valueOf(tintColor))
    }

    class Holder : MerakiEpoxyHolder() {
        val avatar by bind<ImageView>(R.id.bottomSheetRoomPreviewAvatar)
        val roomName by bind<TextView>(R.id.bottomSheetRoomPreviewName)
        val roomFavorite by bind<ImageView>(R.id.bottomSheetRoomPreviewFavorite)
        val roomSettings by bind<View>(R.id.bottomSheetRoomPreviewSettings)
    }
}
