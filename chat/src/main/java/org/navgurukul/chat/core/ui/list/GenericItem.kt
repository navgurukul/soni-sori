package org.navgurukul.chat.core.ui.list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.extensions.setTextOrHide

/**
 * A generic list item.
 * Displays an item with a title, and optional description.
 * Can display an accessory on the right, that can be an image or an indeterminate progress.
 * If provided with an action, will display a button at the bottom of the list item.
 */
@EpoxyModelClass
abstract class GenericItem : MerakiEpoxyModel<GenericItem.Holder>() {

    enum class STYLE {
        BIG_TEXT,
        NORMAL_TEXT
    }

    class Action(var title: String) {
        var perform: Runnable? = null
    }

    @EpoxyAttribute
    var title: String? = null

    @EpoxyAttribute
    var description: CharSequence? = null

    @EpoxyAttribute
    var style: STYLE = STYLE.NORMAL_TEXT

    @EpoxyAttribute
    @DrawableRes
    var endIconResourceId: Int = -1

    @EpoxyAttribute
    @DrawableRes
    var titleIconResourceId: Int = -1

    @EpoxyAttribute
    var hasIndeterminateProcess = false

    @EpoxyAttribute
    var buttonAction: Action? = null

    @EpoxyAttribute
    var itemClickAction: Action? = null

    override fun getDefaultLayout(): Int = R.layout.item_generic_list

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.titleText.setTextOrHide(title)

        if (titleIconResourceId != -1) {
            holder.titleIcon.setImageResource(titleIconResourceId)
            holder.titleIcon.isVisible = true
        } else {
            holder.titleIcon.isVisible = false
        }

        when (style) {
            STYLE.BIG_TEXT    -> holder.titleText.textSize = 18f
            STYLE.NORMAL_TEXT -> holder.titleText.textSize = 14f
        }

        holder.descriptionText.setTextOrHide(description)

        if (hasIndeterminateProcess) {
            holder.progressBar.isVisible = true
            holder.accessoryImage.isVisible = false
        } else {
            holder.progressBar.isVisible = false
            if (endIconResourceId != -1) {
                holder.accessoryImage.setImageResource(endIconResourceId)
                holder.accessoryImage.isVisible = true
            } else {
                holder.accessoryImage.isVisible = false
            }
        }

        holder.actionButton.setTextOrHide(buttonAction?.title)
        holder.actionButton.setOnClickListener {
            buttonAction?.perform?.run()
        }

        holder.root.setOnClickListener {
            itemClickAction?.perform?.run()
        }
    }

    class Holder : MerakiEpoxyHolder() {
        val root by bind<View>(R.id.item_generic_root)
        val titleIcon by bind<ImageView>(R.id.item_generic_title_image)
        val titleText by bind<TextView>(R.id.item_generic_title_text)
        val descriptionText by bind<TextView>(R.id.item_generic_description_text)
        val accessoryImage by bind<ImageView>(R.id.item_generic_accessory_image)
        val progressBar by bind<ProgressBar>(R.id.item_generic_progress_bar)
        val actionButton by bind<Button>(R.id.item_generic_action_button)
    }
}
