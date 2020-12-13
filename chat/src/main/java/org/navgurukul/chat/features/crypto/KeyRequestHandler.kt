package org.navgurukul.chat.features.crypto

import android.content.Context
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.keyshare.GossipingRequestListener
import org.matrix.android.sdk.api.session.crypto.verification.SasVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationService
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import org.matrix.android.sdk.internal.crypto.IncomingRequestCancellation
import org.matrix.android.sdk.internal.crypto.IncomingRoomKeyRequest
import org.matrix.android.sdk.internal.crypto.IncomingSecretShareRequest
import org.matrix.android.sdk.internal.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.internal.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.internal.crypto.model.MXUsersDevicesMap
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceInfo
import org.navgurukul.chat.R
import org.navgurukul.chat.features.popup.DefaultSaralAlert
import org.navgurukul.chat.features.popup.PopupAlertManager
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class KeyRequestHandler (private val context: Context, private val popupAlertManager: PopupAlertManager)
    : GossipingRequestListener,
        VerificationService.Listener {

    private val alertsToRequests = HashMap<String, ArrayList<IncomingRoomKeyRequest>>()

    var session: Session? = null

    fun start(session: Session) {
        this.session = session
        session.cryptoService().verificationService().addListener(this)
        session.cryptoService().addRoomKeysRequestListener(this)
    }

    fun stop() {
        session?.cryptoService()?.verificationService()?.removeListener(this)
        session?.cryptoService()?.removeRoomKeysRequestListener(this)
        session = null
    }

    override fun onSecretShareRequest(request: IncomingSecretShareRequest): Boolean {
        // By default riotX will not prompt if the SDK has decided that the request should not be fulfilled
        Timber.v("## onSecretShareRequest() : Ignoring $request")
        request.ignore?.run()
        return true
    }

    /**
     * Handle incoming key request.
     *
     * @param request the key request.
     */
    override fun onRoomKeyRequest(request: IncomingRoomKeyRequest) {
        val userId = request.userId
        val deviceId = request.deviceId
        val requestId = request.requestId

        if (userId.isNullOrBlank() || deviceId.isNullOrBlank() || requestId.isNullOrBlank()) {
            Timber.e("## handleKeyRequest() : invalid parameters")
            return
        }

        // Do we already have alerts for this user/device
        val mappingKey = keyForMap(userId, deviceId)
        if (alertsToRequests.containsKey(mappingKey)) {
            // just add the request, there is already an alert for this
            alertsToRequests[mappingKey]?.add(request)
            return
        }

        alertsToRequests[mappingKey] = ArrayList<IncomingRoomKeyRequest>().apply { this.add(request) }

        // Add a notification for every incoming request
        session?.cryptoService()?.downloadKeys(listOf(userId), false, object :
            MatrixCallback<MXUsersDevicesMap<CryptoDeviceInfo>> {
            override fun onSuccess(data: MXUsersDevicesMap<CryptoDeviceInfo>) {
                val deviceInfo = data.getObject(userId, deviceId)

                if (null == deviceInfo) {
                    Timber.e("## displayKeyShareDialog() : No details found for device $userId:$deviceId")
                    // ignore
                    return
                }

                if (deviceInfo.isUnknown) {
                    session?.cryptoService()?.setDeviceVerification(DeviceTrustLevel(crossSigningVerified = false, locallyVerified = false), userId, deviceId)

                    deviceInfo.trustLevel = DeviceTrustLevel(crossSigningVerified = false, locallyVerified = false)

                    // can we get more info on this device?
                    session?.cryptoService()?.getMyDevicesInfo()?.firstOrNull { it.deviceId == deviceId }?.let {
                        postAlert(context, userId, deviceId, true, deviceInfo, it)
                    } ?: kotlin.run {
                        postAlert(context, userId, deviceId, true, deviceInfo)
                    }
                } else {
                    postAlert(context, userId, deviceId, false, deviceInfo)
                }
            }

            override fun onFailure(failure: Throwable) {
                // ignore
                Timber.e(failure, "## displayKeyShareDialog : downloadKeys")
            }
        })
    }

    private fun postAlert(context: Context,
                          userId: String,
                          deviceId: String,
                          wasNewDevice: Boolean,
                          deviceInfo: CryptoDeviceInfo?,
                          moreInfo: DeviceInfo? = null) {
        val deviceName = if (deviceInfo!!.displayName().isNullOrEmpty()) deviceInfo.deviceId else deviceInfo.displayName()
        val dialogText: String?

        if (moreInfo != null) {
            val lastSeenIp = if (moreInfo.lastSeenIp.isNullOrBlank()) {
                context.getString(R.string.encryption_information_unknown_ip)
            } else {
                moreInfo.lastSeenIp
            }

            val lastSeenTime = moreInfo.lastSeenTs?.let { ts ->
                val dateFormatTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val date = Date(ts)

                val time = dateFormatTime.format(date)
                val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())

                dateFormat.format(date) + ", " + time
            } ?: "-"

            val lastSeenInfo = context.getString(R.string.devices_details_last_seen_format, lastSeenIp, lastSeenTime)
            dialogText = if (wasNewDevice) {
                context.getString(R.string.you_added_a_new_device_with_info, deviceName, lastSeenInfo)
            } else {
                context.getString(R.string.your_unverified_device_requesting_with_info, deviceName, lastSeenInfo)
            }
        } else {
            dialogText = if (wasNewDevice) {
                context.getString(R.string.you_added_a_new_device, deviceName)
            } else {
                context.getString(R.string.your_unverified_device_requesting, deviceName)
            }
        }

        val alert = DefaultSaralAlert(
                alertManagerId(userId, deviceId),
                context.getString(R.string.key_share_request),
                dialogText,
                R.drawable.ic_key
        )

        alert.colorRes = R.color.notification_accent_color

        val mappingKey = keyForMap(userId, deviceId)
        alert.dismissedAction = Runnable {
            denyAllRequests(mappingKey)
        }

        alert.addButton(context.getString(R.string.share_without_verifying_short_label), Runnable {
            shareAllSessions(mappingKey)
        })

        alert.addButton(context.getString(R.string.ignore_request_short_label), Runnable {
            denyAllRequests(mappingKey)
        })

        popupAlertManager.postVectorAlert(alert)
    }

    private fun denyAllRequests(mappingKey: String) {
        alertsToRequests[mappingKey]?.forEach {
            it.ignore?.run()
        }
        alertsToRequests.remove(mappingKey)
    }

    private fun shareAllSessions(mappingKey: String) {
        alertsToRequests[mappingKey]?.forEach {
            it.share?.run()
        }
        alertsToRequests.remove(mappingKey)
    }

    /**
     * Manage a cancellation request.
     *
     * @param request the cancellation request.
     */
    override fun onRoomKeyRequestCancellation(request: IncomingRequestCancellation) {
        // see if we can find the request in the queue
        val userId = request.userId
        val deviceId = request.deviceId
        val requestId = request.requestId

        if (userId.isNullOrEmpty() || deviceId.isNullOrEmpty() || requestId.isNullOrEmpty()) {
            Timber.e("## handleKeyRequestCancellation() : invalid parameters")
            return
        }

        val alertMgrUniqueKey = alertManagerId(userId, deviceId)
        alertsToRequests[alertMgrUniqueKey]?.removeAll {
            it.deviceId == request.deviceId
                    && it.userId == request.userId
                    && it.requestId == request.requestId
        }
        if (alertsToRequests[alertMgrUniqueKey]?.isEmpty() == true) {
            popupAlertManager.cancelAlert(alertMgrUniqueKey)
            alertsToRequests.remove(keyForMap(userId, deviceId))
        }
    }

    override fun transactionUpdated(tx: VerificationTransaction) {
        if (tx is SasVerificationTransaction) {
            val state = tx.state
            if (state == VerificationTxState.Verified) {
                // ok it's verified, see if we have key request for that
                shareAllSessions("${tx.otherDeviceId}${tx.otherUserId}")
                popupAlertManager.cancelAlert("ikr_${tx.otherDeviceId}${tx.otherUserId}")
            }
        }
        // should do it with QR tx also
        // TODO -> Probably better to listen to device trust changes?
    }

    override fun markedAsManuallyVerified(userId: String, deviceId: String) {
        // accept related requests
        shareAllSessions(keyForMap(userId, deviceId))
        popupAlertManager.cancelAlert(alertManagerId(userId, deviceId))
    }

    private fun keyForMap(userId: String, deviceId: String) = "$deviceId$userId"

    private fun alertManagerId(userId: String, deviceId: String) = "ikr_$deviceId$userId"
}