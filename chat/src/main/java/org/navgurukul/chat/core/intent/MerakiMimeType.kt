package org.navgurukul.chat.core.intent

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import org.navgurukul.chat.core.utils.getFileExtension
import timber.log.Timber

/**
 * Returns the mimetype from a uri.
 *
 * @param context the context
 * @return the mimetype
 */
fun getMimeTypeFromUri(context: Context, uri: Uri): String? {
    var mimeType: String? = null

    try {
        mimeType = context.contentResolver.getType(uri)

        // try to find the mimetype from the filename
        if (null == mimeType) {
            val extension = getFileExtension(uri.toString())
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        }

        if (null != mimeType) {
            // the mimetype is sometimes in uppercase.
            mimeType = mimeType.toLowerCase()
        }
    } catch (e: Exception) {
        Timber.e(e, "Failed to open resource input stream")
    }

    return mimeType
}
