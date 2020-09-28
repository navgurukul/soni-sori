package org.merakilearn.core.di

import org.koin.dsl.module
import org.merakilearn.core.navigator.MerakiNavigator

private val module = module {
    single { MerakiNavigator(get(), get()) }
}

val coreModules = arrayListOf(module)