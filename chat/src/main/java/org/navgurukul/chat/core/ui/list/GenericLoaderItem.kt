package org.navgurukul.chat.core.ui.list

import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder
import org.navgurukul.chat.core.epoxy.MerakiEpoxyModel

/**
 * A generic list item header left aligned with notice color.
 */
@EpoxyModelClass
abstract class GenericLoaderItem : MerakiEpoxyModel<GenericLoaderItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_generic_loader
    // Maybe/Later add some style configuration, SMALL/BIG ?

    class Holder : MerakiEpoxyHolder()
}