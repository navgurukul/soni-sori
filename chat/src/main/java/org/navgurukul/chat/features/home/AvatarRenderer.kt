/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.navgurukul.chat.features.home

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import im.vector.matrix.android.api.session.content.ContentUrlResolver
import im.vector.matrix.android.api.util.MatrixItem
import org.navgurukul.chat.core.extensions.getBitmap
import org.navgurukul.chat.core.extensions.getBitmapFromCache
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
    fun render(matrixItem: MatrixItem, imageView: SimpleDraweeView) {
        val placeholder = getPlaceholderDrawable(imageView.context, matrixItem)
        val resolvedUrl = resolvedUrl(matrixItem.avatarUrl)
        imageView.hierarchy.setPlaceholderImage(placeholder)
        imageView.setImageURI(resolvedUrl)
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

    fun getAvatarDrawable(context: Context, matrixItem: MatrixItem, callback: (BitmapDrawable?) -> Unit) {
        val resolvedUrl = resolvedUrl(matrixItem.avatarUrl)
        ImageRequest.fromUri(resolvedUrl)?.getBitmap(context, callback)
    }

    fun getCachedAvatarDrawable(context: Context, matrixItem: MatrixItem): BitmapDrawable? {
        val resolvedUrl = resolvedUrl(matrixItem.avatarUrl)
        return ImageRequest.fromUri(resolvedUrl)?.getBitmapFromCache(context)
    }

    // PRIVATE API *********************************************************************************


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
