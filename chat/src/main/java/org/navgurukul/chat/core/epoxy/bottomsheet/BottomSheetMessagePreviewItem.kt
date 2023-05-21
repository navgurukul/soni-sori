package org.navgurukul.chat.core.epoxy.bottomsheet

import android.text.method.MovementMethod
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.extensions.setTextOrHide
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.detail.timeline.tools.findPillsAndProcess

/**
 * A message preview for bottom sheet.
 */
@EpoxyModelClass
abstract class BottomSheetMessagePreviewItem : MerakiEpoxyModel<BottomSheetMessagePreviewItem.Holder>() {

    @EpoxyAttribute
    lateinit var avatarRenderer: AvatarRenderer
    @EpoxyAttribute
    lateinit var matrixItem: MatrixItem
    @EpoxyAttribute
    lateinit var body: CharSequence
    @EpoxyAttribute
    var time: CharSequence? = null
    @EpoxyAttribute
    var movementMethod: MovementMethod? = null
    @EpoxyAttribute
    var userClicked: (() -> Unit)? = null

    override fun getDefaultLayout(): Int = R.layout.item_bottom_sheet_message_preview

    override fun bind(holder: Holder) {
        super.bind(holder)
        avatarRenderer.render(matrixItem, holder.avatar)
        holder.avatar.setOnClickListener { userClicked?.invoke() }
        holder.sender.setTextOrHide(matrixItem.displayName)
        holder.body.movementMethod = movementMethod
        holder.body.text = body
        body.findPillsAndProcess(coroutineScope) { it.bind(holder.body) }
        holder.timestamp.setTextOrHide(time)
    }

    class Holder : MerakiEpoxyHolder() {
        val avatar by bind<ImageView>(R.id.bottom_sheet_message_preview_avatar)
        val sender by bind<TextView>(R.id.bottom_sheet_message_preview_sender)
        val body by bind<TextView>(R.id.bottom_sheet_message_preview_body)
        val timestamp by bind<TextView>(R.id.bottom_sheet_message_preview_timestamp)
    }
}
