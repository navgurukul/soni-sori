package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel

@EpoxyModelClass
abstract class RoomCreateItem : MerakiEpoxyModel<RoomCreateItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_create

    @EpoxyAttribute
    lateinit var text: CharSequence

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.description.movementMethod = BetterLinkMovementMethod.getInstance()
        holder.description.text = text
    }

    class Holder : MerakiEpoxyHolder() {
        val description by bind<TextView>(R.id.roomCreateItemDescription)
    }
}