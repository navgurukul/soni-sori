package org.navgurukul.chat.core.epoxy.profiles

import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R

@EpoxyModelClass
abstract class ProfileMatrixItemWithProgress : BaseProfileMatrixItem<ProfileMatrixItemWithProgress.Holder>() {
    override fun getDefaultLayout(): Int = R.layout.item_profile_matrix_item_progress

    @EpoxyAttribute var inProgress: Boolean = true

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.progress.isVisible = inProgress
    }

    class Holder : ProfileMatrixItem.Holder() {
        val progress by bind<ProgressBar>(R.id.matrixItemProgress)
    }
}
