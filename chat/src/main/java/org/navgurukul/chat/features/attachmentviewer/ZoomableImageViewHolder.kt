package org.navgurukul.chat.features.attachmentviewer

import android.view.View
import android.widget.ProgressBar
import com.github.chrisbanes.photoview.PhotoView
import org.navgurukul.chat.R

class ZoomableImageViewHolder constructor(itemView: View) :
        BaseViewHolder(itemView) {

    val touchImageView: PhotoView = itemView.findViewById(R.id.touchImageView)
    val imageLoaderProgress: ProgressBar = itemView.findViewById(R.id.imageLoaderProgress)

    init {
        touchImageView.setAllowParentInterceptOnEdge(false)
        touchImageView.setOnScaleChangeListener { scaleFactor, _, _ ->
            // Log.v("ATTACHEMENTS", "scaleFactor $scaleFactor")
            // It's a bit annoying but when you pitch down the scaling
            // is not exactly one :/
            touchImageView.setAllowParentInterceptOnEdge(scaleFactor <= 1.0008f)
        }
        touchImageView.setScale(1.0f, true)
        touchImageView.setAllowParentInterceptOnEdge(true)
    }

    internal val target = DefaultImageLoaderTarget.ZoomableImageTarget(this, touchImageView)

    override fun onRecycled() {
        super.onRecycled()
        touchImageView.setImageDrawable(null)
    }
}
