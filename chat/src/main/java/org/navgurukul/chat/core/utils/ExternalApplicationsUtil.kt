package org.navgurukul.chat.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.navgurukul.chat.BuildConfig
import org.navgurukul.chat.R
import timber.log.Timber
import java.io.File

fun shareMedia(context: Context, file: File, mediaMimeType: String?) {
    var mediaUri: Uri? = null
    try {
        mediaUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file)
    } catch (e: Exception) {
        Timber.e(e, "onMediaAction Selected File cannot be shared")
    }

    if (null != mediaUri) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        // Grant temporary read permission to the content URI
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.type = mediaMimeType
        sendIntent.putExtra(Intent.EXTRA_STREAM, mediaUri)

        try {
            context.startActivity(sendIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            context.toast(R.string.error_no_external_application_found)
        }
    }
}

