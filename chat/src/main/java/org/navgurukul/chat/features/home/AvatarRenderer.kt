package org.navgurukul.chat.features.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.util.MatrixItem
import org.navgurukul.chat.core.glide.GlideApp
import org.navgurukul.chat.core.glide.GlideRequest
import org.navgurukul.chat.core.glide.GlideRequests
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.utils.getColorFromRoomId
import org.navgurukul.chat.core.utils.getColorFromUserId

/**
 * This helper centralise ways to retrieve avatar into ImageView or even generic Target<Drawable>
 */

class AvatarRenderer(private val activeSessionHolder: ActiveSessionHolder) {

    companion object {
        private const val THUMBNAIL_SIZE = 250
    }

    @UiThread
    fun render(matrixItem: MatrixItem, imageView: ImageView) {
        render(imageView.context,
            GlideApp.with(imageView),
            matrixItem,
            DrawableImageViewTarget(imageView)
        )
    }

    @UiThread
    fun render(matrixItem: MatrixItem, imageView: ImageView, glideRequests: GlideRequests) {
        render(imageView.context,
            glideRequests,
            matrixItem,
            DrawableImageViewTarget(imageView)
        )
    }

    @UiThread
    fun render(context: Context,
               glideRequests: GlideRequests,
               matrixItem: MatrixItem,
               target: Target<Drawable>
    ) {
        val placeholder = getPlaceholderDrawable(context, matrixItem)
        buildGlideRequest(glideRequests, matrixItem.avatarUrl)
            .placeholder(placeholder)
            .into(target)
    }

    @AnyThread
    @Throws
    fun shortcutDrawable(context: Context, glideRequests: GlideRequests, matrixItem: MatrixItem, iconSize: Int): Bitmap {
        return glideRequests
            .asBitmap()
            .apply {
                val resolvedUrl = resolvedUrl(matrixItem.avatarUrl)
                if (resolvedUrl != null) {
                    load(resolvedUrl)
                } else {
                    val avatarColor = avatarColor(matrixItem, context)
                    load(TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        .endConfig()
                        .buildRect(matrixItem.firstLetterOfDisplayName(), avatarColor)
                        .toBitmap(width = iconSize, height = iconSize))
                }
            }
            .submit(iconSize, iconSize)
            .get()
    }

    @AnyThread
    fun getCachedDrawable(glideRequests: GlideRequests, matrixItem: MatrixItem): Drawable {
        return buildGlideRequest(glideRequests, matrixItem.avatarUrl)
            .onlyRetrieveFromCache(true)
            .submit()
            .get()
    }

    @AnyThread
    fun getPlaceholderDrawable(context: Context, matrixItem: MatrixItem): Drawable {
        val avatarColor = avatarColor(matrixItem, context)
        return TextDrawable.builder()
            .beginConfig()
            .bold()
            .endConfig()
            .buildRound(matrixItem.firstLetterOfDisplayName(), avatarColor)
    }

    // PRIVATE API *********************************************************************************

    private fun buildGlideRequest(glideRequests: GlideRequests, avatarUrl: String?): GlideRequest<Drawable> {
        val resolvedUrl = resolvedUrl(avatarUrl)
        return glideRequests
            .load(resolvedUrl)
            .apply(RequestOptions.circleCropTransform())
    }

    private fun resolvedUrl(avatarUrl: String?): String? {
        return activeSessionHolder.getSafeActiveSession()?.contentUrlResolver()
            ?.resolveThumbnail(avatarUrl, THUMBNAIL_SIZE, THUMBNAIL_SIZE, ContentUrlResolver.ThumbnailMethod.SCALE)
    }

    private fun avatarColor(matrixItem: MatrixItem, context: Context): Int {
        return when (matrixItem) {
            is MatrixItem.UserItem -> ContextCompat.getColor(context, getColorFromUserId(matrixItem.id))
            else                   -> ContextCompat.getColor(context, getColorFromRoomId(matrixItem.id))
        }
    }
}
