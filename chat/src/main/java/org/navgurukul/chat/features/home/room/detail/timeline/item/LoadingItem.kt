package org.navgurukul.chat.features.home.room.detail.timeline.item

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel
import org.navgurukul.chat.core.extensions.setTextOrHide

@EpoxyModelClass
abstract class LoadingItem : MerakiEpoxyModel<LoadingItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_loading

    @EpoxyAttribute
    var loadingText: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.textView.setTextOrHide(loadingText)
    }

    class Holder : MerakiEpoxyHolder() {
        val textView by bind<TextView>(R.id.loadingText)
    }
}