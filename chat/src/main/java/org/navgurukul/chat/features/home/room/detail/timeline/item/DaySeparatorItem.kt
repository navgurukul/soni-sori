package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder

@EpoxyModelClass
abstract class DaySeparatorItem : EpoxyModelWithHolder<DaySeparatorItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_day_separator

    @EpoxyAttribute
    lateinit var formattedDay: CharSequence

    override fun bind(holder: Holder) {

        super.bind(holder)
        holder.dayTextView.text = formattedDay
    }

    class Holder : MerakiEpoxyHolder() {
        val dayTextView by bind<TextView>(R.id.itemDayTextView)
    }
}
