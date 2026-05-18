package org.merakilearn.core.impl

import com.google.firebase.messaging.RemoteMessage
import org.merakilearn.core.push.FCMServiceDelegate
import timber.log.Timber

class FCMServiceDelegateImpl : FCMServiceDelegate {
    override fun onNewToken(refreshedToken: String) {
        // Your implementation here
        Timber.d("New token: $refreshedToken")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Your implementation here
        Timber.d("Message received: ${message.messageId}")
    }
}