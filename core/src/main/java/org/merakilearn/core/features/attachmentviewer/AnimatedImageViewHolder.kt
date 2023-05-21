package org.merakilearn.core.features.attachmentviewer

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import org.navgurukul.chat.R

class AnimatedImageViewHolder constructor(itemView: View) :
        org.merakilearn.core.features.attachmentviewer.BaseViewHolder(itemView) {

    val touchImageView: ImageView = itemView.findViewById(R.id.imageView)
    val imageLoaderProgress: ProgressBar = itemView.findViewById(R.id.imageLoaderProgress)

    internal val target = org.merakilearn.core.features.attachmentviewer.DefaultImageLoaderTarget(
        this,
        this.touchImageView
    )

    override fun onRecycled() {
        super.onRecycled()
        touchImageView.setImageDrawable(null)
    }
}
