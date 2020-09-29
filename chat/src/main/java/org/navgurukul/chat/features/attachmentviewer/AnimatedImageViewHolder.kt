package org.navgurukul.chat.features.attachmentviewer

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import org.navgurukul.chat.R

class AnimatedImageViewHolder constructor(itemView: View) :
        BaseViewHolder(itemView) {

    val touchImageView: ImageView = itemView.findViewById(R.id.imageView)
    val imageLoaderProgress: ProgressBar = itemView.findViewById(R.id.imageLoaderProgress)

    internal val target = DefaultImageLoaderTarget(this, this.touchImageView)

    override fun onRecycled() {
        super.onRecycled()
        touchImageView.setImageDrawable(null)
    }
}
