package org.merakilearn.core.dynamic.module

import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger

class DynamicFeatureModuleManager(private val splitInstallManager: SplitInstallManager) {
    sealed class DynamicDeliveryProgress {

        object Pending : DynamicDeliveryProgress()

        object Installing : DynamicDeliveryProgress()

        data class RequiresConfirmation(
            val state: SplitInstallSessionState
        ) : DynamicDeliveryProgress()

        data class Downloading(
            val totalBytes: Long,
            val currentBytes: Long
        ) : DynamicDeliveryProgress()
    }

    fun isInstalled(moduleName: String): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    fun installModule(
        moduleName: String,
        success: (() -> Unit)? = null,
        error: ((code: Int) -> Unit)? = null,
        progress: ((DynamicDeliveryProgress) -> Unit)? = null
    ) {

        // If module is installed, complete immediately
        if (isInstalled(moduleName)) {
            success?.invoke()
            return
        }
        // Session identifier
        val sessionId = AtomicInteger()
        // Create progress listener and register it

        var listener: SplitInstallStateUpdatedListener? = null
        listener = createListener(
            sessionId = sessionId,
            progress = progress,
            onComplete = {
                if (it == null) {
                    success?.invoke()
                } else {
                    error?.invoke((it as? SplitInstallException)?.errorCode ?: 0)
                }
                splitInstallManager.unregisterListener(listener!!)
            }
        )
        splitInstallManager.registerListener(listener)

        // Create new installation request
        val request = SplitInstallRequest
            .newBuilder()
            .addModule(moduleName)
            .build()
        // startInstall returns Task<Int> with identifier or error
        splitInstallManager.startInstall(request)
            .addOnSuccessListener {
                progress?.invoke(DynamicDeliveryProgress.Pending)
                sessionId.set(it)
            }
            .addOnFailureListener {
                splitInstallManager.unregisterListener(listener)
                error?.invoke((it as? SplitInstallException)?.errorCode ?: 0)
            }


    }

    private fun createListener(
        sessionId: AtomicInteger,
        progress: ((DynamicDeliveryProgress) -> Unit)?,
        onComplete: (Exception?) -> Unit
    ) =
        SplitInstallStateUpdatedListener {
            if (it.sessionId() == sessionId.get()) {
                when (it.status()) {
                    SplitInstallSessionStatus.PENDING ->
                        progress?.invoke(DynamicDeliveryProgress.Pending)
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION ->
                        // Confirmation is required
                        progress?.invoke(DynamicDeliveryProgress.RequiresConfirmation(it))
                    SplitInstallSessionStatus.DOWNLOADING ->
                        progress?.invoke(
                            DynamicDeliveryProgress.Downloading(
                                totalBytes = it.totalBytesToDownload(),
                                currentBytes = it.bytesDownloaded()
                            )
                        )
                    SplitInstallSessionStatus.DOWNLOADED -> {
                        onComplete(null)
                    }
                    SplitInstallSessionStatus.INSTALLING ->
                        progress?.invoke(DynamicDeliveryProgress.Installing)
                    SplitInstallSessionStatus.INSTALLED ->
                        onComplete(null)
                    SplitInstallSessionStatus.CANCELED -> {
                        onComplete(CancellationException())
                    }
                    SplitInstallSessionStatus.CANCELING,
                    SplitInstallSessionStatus.UNKNOWN,
                    SplitInstallSessionStatus.FAILED -> Unit
                }
            }
        }
}