package org.merakilearn.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.java.KoinJavaComponent.inject
import org.merakilearn.MainActivity
import org.merakilearn.R
import org.merakilearn.core.push.FCMServiceDelegate
import timber.log.Timber

class MerakiMessagingService : FirebaseMessagingService() {

    private val fcmServiceDelegate: FCMServiceDelegate by inject(FCMServiceDelegate::class.java)


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.d("From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification.let {
            Timber.d("Message Notification Body: ${it?.body}")
            sendNotification(it?.title, it?.body, it?.imageUrl)
        }

        fcmServiceDelegate.onMessageReceived(remoteMessage)
    }


    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        sendRegistrationToServer(token)
        fcmServiceDelegate.onNewToken(token)
    }


    private fun sendRegistrationToServer(token: String?) {
        Timber.d("sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        imageUrl: Uri?
    ) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notificationBuilder.setLargeIcon(resource)
                    notificationManager.notify(0, notificationBuilder.build())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    AppCompatResources.getDrawable(this@MerakiMessagingService,
                        R.mipmap.ic_launcher)?.let {
                        notificationBuilder.setLargeIcon(it.toBitmap())
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    AppCompatResources.getDrawable(this@MerakiMessagingService,
                        R.mipmap.ic_launcher)?.let {
                        notificationBuilder.setLargeIcon(it.toBitmap())
                    }
                    notificationManager.notify(0, notificationBuilder.build())
                }

            })
    }

    companion object {

        private const val TAG = "MerakiMessagingService"
    }
}