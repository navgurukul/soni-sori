package org.merakilearn

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.merakilearn.di.appModules
import org.merakilearn.di.learnModules
import org.navgurukul.chat.core.ChatInitializer
import org.navgurukul.chat.core.di.chatModules
import org.navgurukul.playground.di.playgroundModules

class SaralApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SaralApp)
            androidLogger()
            modules(appModules + chatModules + learnModules + playgroundModules)
        }

        ChatInitializer.initialise(this)
        subscribeToDefaultTopic()
    }

    private fun subscribeToDefaultTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID)
            .addOnCompleteListener { task ->
                Log.d(TAG, "subscribeToDefaultTopic: ")
            }
    }

    companion object {
        private const val TAG = "SaralApp"
    }
}