package org.navgurukul.saral

import android.app.Application
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.navgurukul.chat.core.di.chatModules
import org.navgurukul.chat.core.repo.ChatRepository
import org.navgurukul.learn.di.learnModules
import org.navgurukul.playground.di.playgroundModules
import org.navgurukul.saral.di.appModules

class SaralApp: Application() {

    private val chatRepository: ChatRepository by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SaralApp)
            androidLogger()
            modules(appModules + chatModules + learnModules + playgroundModules)
        }

        chatRepository.initialise()
    }
}