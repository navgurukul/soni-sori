package org.navgurukul.chat.features.push

import android.app.Activity
import android.content.Context
import androidx.preference.PreferenceManager
import android.widget.Toast
import androidx.core.content.edit
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import org.navgurukul.chat.R
import org.navgurukul.chat.core.pushers.PushersManager
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.settings.ChatPreferences
import timber.log.Timber

/**
 * This class store the FCM token in SharedPrefs and ensure this token is retrieved.
 * It has an alter ego in the fdroid variant.
 */
class FcmHelper {
    private val PREFS_KEY_FCM_TOKEN = "FCM_TOKEN"

    /**
     * Retrieves the FCM registration token.
     *
     * @return the FCM token or null if not received from FCM
     */
    fun getFcmToken(context: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFS_KEY_FCM_TOKEN, null)
    }

    /**
     * Store FCM token to the SharedPrefs
     * TODO Store in realm
     *
     * @param context android context
     * @param token   the token to store
     */
    fun storeFcmToken(context: Context,
                      token: String?) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PREFS_KEY_FCM_TOKEN, token)
        }
    }

    /**
     * onNewToken may not be called on application upgrade, so ensure my shared pref is set
     *
     * @param activity the first launch Activity
     */
    fun ensureFcmTokenIsRetrieved(activity: Activity, pushersManager: PushersManager, registerPusher: Boolean) {
        //        if (TextUtils.isEmpty(getFcmToken(activity))) {
        // 'app should always check the device for a compatible Google Play services APK before accessing Google Play services features'
        if (checkPlayServices(activity)) {
            try {
                FirebaseMessaging.getInstance().token
                        .addOnSuccessListener(activity) { it ->
                            storeFcmToken(activity, it)
                            if (registerPusher) {
                                pushersManager.registerPusherWithFcmKey(it)
                            }
                        }
                        .addOnFailureListener(activity) { e -> Timber.e(e, "## ensureFcmTokenIsRetrieved() : failed") }
            } catch (e: Throwable) {
                Timber.e(e, "## ensureFcmTokenIsRetrieved() : failed")
            }
        } else {
            Toast.makeText(activity, R.string.no_valid_google_play_services_apk, Toast.LENGTH_SHORT).show()
            Timber.e("No valid Google Play Services found. Cannot use FCM.")
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private fun checkPlayServices(activity: Activity): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(activity)
        return resultCode == ConnectionResult.SUCCESS
    }
}
