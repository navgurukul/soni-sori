package org.merakilearn.core.push

import com.google.firebase.messaging.RemoteMessage

interface FCMServiceDelegate {
    fun onNewToken(refreshedToken: String)
    fun onMessageReceived(message: RemoteMessage)
}