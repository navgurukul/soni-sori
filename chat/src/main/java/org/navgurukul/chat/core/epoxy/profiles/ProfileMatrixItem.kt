package org.navgurukul.chat.core.epoxy.profiles

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.MerakiEpoxyHolder

@EpoxyModelClass
abstract class ProfileMatrixItem : BaseProfileMatrixItem<ProfileMatrixItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_profile_matrix_item

    open class Holder : MerakiEpoxyHolder() {
        val titleView by bind<TextView>(R.id.matrixItemTitle)
        val subtitleView by bind<TextView>(R.id.matrixItemSubtitle)
        val avatarImageView by bind<ImageView>(R.id.matrixItemAvatar)
        val editableView by bind<View>(R.id.matrixItemEditable)
    }
}
