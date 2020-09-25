package org.navgurukul.chat.features.home.room.detail.timeline.item

import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel

@EpoxyModelClass
abstract class TimelineReadMarkerItem : MerakiEpoxyModel<TimelineReadMarkerItem.Holder>() {
    
    override fun getDefaultLayout(): Int = R.layout.item_timeline_read_marker
    
    class Holder : MerakiEpoxyHolder()
}