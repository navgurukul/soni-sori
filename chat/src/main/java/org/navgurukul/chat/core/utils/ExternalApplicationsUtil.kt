package org.navgurukul.chat.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import androidx.core.content.FileProvider
import org.merakilearn.core.extentions.toast
import org.navgurukul.chat.R
import timber.log.Timber
import java.io.File

/**
 * Open a url in the internet browser of the system
 */
fun openUrlInExternalBrowser(context: Context, url: String?) {
    url?.let {
        openUrlInExternalBrowser(context, Uri.parse(it))
    }
}

/**
 * Open a uri in the internet browser of the system
 */
fun openUrlInExternalBrowser(context: Context, uri: Uri?) {
    uri?.let {
        val browserIntent = Intent(Intent.ACTION_VIEW, it).apply {
            putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
        }

        try {
            context.startActivity(browserIntent)
        } catch (activityNotFoundException: ActivityNotFoundException) {
            context.toast(R.string.error_no_external_application_found)
        }
    }
}


fun shareMedia(context: Context, file: File, mediaMimeType: String?) {
    var mediaUri: Uri? = null
    try {
        mediaUri = FileProvider.getUriForFile(context, context.packageName + ".fileProvider", file)
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

