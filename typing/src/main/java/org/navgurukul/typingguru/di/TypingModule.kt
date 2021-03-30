package org.navgurukul.typingguru.di

import org.koin.dsl.module
import org.navgurukul.typingguru.utils.TypingGuruPreferenceManager
import org.navgurukul.typingguru.utils.Utility

val typingModule = module {
    single { Utility() }
    single { TypingGuruPreferenceManager(get()) }
}