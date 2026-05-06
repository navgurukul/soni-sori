package org.merakilearn

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.merakilearn.core.di.coreModules
import org.merakilearn.core.datasource.Config
import org.merakilearn.di.appModules
//import org.navgurukul.chat.core.ChatInitializer
//import org.navgurukul.chat.core.di.chatModules
import org.navgurukul.commonui.di.commonUIModules
import org.navgurukul.learn.di.learnModules
import org.navgurukul.playground.di.playgroundModules
import timber.log.Timber

class MerakiApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MerakiApp)
            modules(appModules + learnModules + playgroundModules + commonUIModules + coreModules)
        }

        // Use google-services.json for standard Firebase init (required for Crashlytics build ID)
        if (org.merakilearn.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }

        val config: Config by inject()
        config.initialise()

//        ChatInitializer.initialise(this)
        subscribeToDefaultTopic()

        val installReferrerManager: InstallReferrerManager by inject()
        installReferrerManager.checkReferrer()

    }

    private fun subscribeToDefaultTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(applicationContext.packageName)
            .addOnCompleteListener { task ->
                Timber.d("subscribeToDefaultTopic: ")
            }
    }
}