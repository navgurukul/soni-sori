package org.navgurukul.chat.core.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.sync.FilterService
import org.navgurukul.chat.core.services.SaralSyncService
import timber.log.Timber

fun Session.configureAndStart(context: Context) {
    Timber.i("Configure and start session for $myUserId")
    open()
    setFilter(FilterService.FilterPreset.RiotFilter)
    startSyncing(context)
    refreshPushers()
}

fun Session.startSyncing(context: Context) {
    val applicationContext = context.applicationContext
    if (!hasAlreadySynced()) {
        // initial sync is done as a service so it can continue below app lifecycle
        SaralSyncService.newOneShotIntent(applicationContext, sessionId, 0).also {
            try {
                ContextCompat.startForegroundService(applicationContext, it)
            } catch (ex: Throwable) {
                // TODO
                Timber.e(ex)
            }
        }
    } else {
        val isAtLeastStarted = ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        Timber.v("--> is at least started? $isAtLeastStarted")
        startSync(isAtLeastStarted)
    }
}

/**
 * Tell is the session has unsaved e2e keys in the backup
 */
fun Session.hasUnsavedKeys(): Boolean {
    return cryptoService().inboundGroupSessionsCount(false) > 0
            && cryptoService().keysBackupService().state != KeysBackupState.ReadyToBackUp
}

fun Session.cannotLogoutSafely(): Boolean {
    // has some encrypted chat
    return hasUnsavedKeys()
            // has local cross signing keys
            || (cryptoService().crossSigningService().allPrivateKeysKnown()
            // That are not backed up
            && !sharedSecretStorageService.isRecoverySetup())
}
