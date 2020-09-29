package org.navgurukul.chat.features.attachmentviewer

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams

interface ImageLoaderTarget {

    fun contextView(): ImageView

    fun onResourceLoading(uid: String, placeholder: Drawable?)

    fun onLoadFailed(uid: String, errorDrawable: Drawable?)

    fun onResourceCleared(uid: String, placeholder: Drawable?)

    fun onResourceReady(uid: String, resource: Drawable)
}

internal class DefaultImageLoaderTarget(val holder: AnimatedImageViewHolder, private val contextView: ImageView)
    : ImageLoaderTarget {
    override fun contextView(): ImageView {
        return contextView
    }

    override fun onResourceLoading(uid: String, placeholder: Drawable?) {
        if (holder.boundResourceUid != uid) return
        holder.imageLoaderProgress.isVisible = true
    }

    override fun onLoadFailed(uid: String, errorDrawable: Drawable?) {
        if (holder.boundResourceUid != uid) return
        holder.imageLoaderProgress.isVisible = false
        holder.touchImageView.setImageDrawable(errorDrawable)
    }

    override fun onResourceCleared(uid: String, placeholder: Drawable?) {
        if (holder.boundResourceUid != uid) return
        holder.touchImageView.setImageDrawable(placeholder)
    }

    override fun onResourceReady(uid: String, resource: Drawable) {
        if (holder.boundResourceUid != uid) return
        holder.imageLoaderProgress.isVisible = false
        // Glide mess up the view size :/
        holder.touchImageView.updateLayoutParams {
            width = LinearLayout.LayoutParams.MATCH_PARENT
            height = LinearLayout.LayoutParams.MATCH_PARENT
        }
        holder.touchImageView.setImageDrawable(resource)
        if (resource is Animatable) {
            resource.start()
        }
    }

    internal class ZoomableImageTarget(val holder: ZoomableImageViewHolder, private val contextView: ImageView) :
        ImageLoaderTarget {
        override fun contextView() = contextView

        override fun onResourceLoading(uid: String, placeholder: Drawable?) {
            if (holder.boundResourceUid != uid) return
            holder.imageLoaderProgress.isVisible = true
            holder.touchImageView.setImageDrawable(placeholder)
        }

        override fun onLoadFailed(uid: String, errorDrawable: Drawable?) {
            if (holder.boundResourceUid != uid) return
            holder.imageLoaderProgress.isVisible = false
            holder.touchImageView.setImageDrawable(errorDrawable)
        }

        override fun onResourceCleared(uid: String, placeholder: Drawable?) {
            if (holder.boundResourceUid != uid) return
            holder.touchImageView.setImageDrawable(placeholder)
        }

        override fun onResourceReady(uid: String, resource: Drawable) {
            if (holder.boundResourceUid != uid) return
            holder.imageLoaderProgress.isVisible = false
            // Glide mess up the view size :/
            holder.touchImageView.updateLayoutParams {
                width = LinearLayout.LayoutParams.MATCH_PARENT
                height = LinearLayout.LayoutParams.MATCH_PARENT
            }
            holder.touchImageView.setImageDrawable(resource)
        }
    }
}