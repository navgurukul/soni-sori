package org.merakilearn

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.merakilearn.core.di.coreModules
import org.merakilearn.core.datasource.Config
import org.merakilearn.di.appModules
import org.navgurukul.chat.core.ChatInitializer
import org.navgurukul.chat.core.di.chatModules
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
            androidContext(this@MerakiApp)
            androidLogger()
            modules(appModules + chatModules + learnModules + playgroundModules + commonUIModules + coreModules)
        }

        val builder: FirebaseOptions.Builder = FirebaseOptions.Builder()
            .setApplicationId("1:449891326531:android:fee3688544a641dd2412f6")
            .setApiKey("AIzaSyCZCxedfsE8RAhAP7q1HNWz9VEJmIizKdw")
            .setDatabaseUrl("https://meraki-c6769.firebaseio.com")
            .setProjectId("meraki-c6769")
            .setStorageBucket("meraki-c6769.appspot.com")
        FirebaseApp.initializeApp(this, builder.build())
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }else { // release or any other variant
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }

        val config: Config by inject()
        config.initialise()

        ChatInitializer.initialise(this)
        subscribeToDefaultTopic()

        val installReferrerManager: InstallReferrerManager by inject()
        installReferrerManager.checkReferrer()

    }

    private fun subscribeToDefaultTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.APPLICATION_ID)
            .addOnCompleteListener { task ->
                Timber.d("subscribeToDefaultTopic: ")
            }
    }
}