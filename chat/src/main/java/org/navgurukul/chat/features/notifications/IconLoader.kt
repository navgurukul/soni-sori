package org.navgurukul.chat.features.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber

class IconLoader(private val context: Context) {

    /**
     * Avatar Url -> IconCompat
     */
    private val cache = HashMap<String, IconCompat?>()

    /**
     * Get icon of a user.
     * If already in cache, use it, else load it and call IconLoaderListener.onIconsLoaded() when ready
     * Before Android P, this does nothing because the icon won't be used
     */
    @WorkerThread
    fun getUserIcon(path: String?): IconCompat? {
        if (path == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return null
        }

        return cache.getOrPut(path) {
            loadUserIcon(path)
        }
    }

    @WorkerThread
    private fun loadUserIcon(path: String): IconCompat? {
        return path.let {
            try {
                Glide.with(context)
                        .asBitmap()
                        .load(path)
                        .apply(RequestOptions.circleCropTransform()
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .submit()
                        .get()
            } catch (e: Exception) {
                Timber.e(e, "decodeFile failed")
                null
            }?.let { bitmap ->
                IconCompat.createWithBitmap(bitmap)
            }
        }
    }
}
