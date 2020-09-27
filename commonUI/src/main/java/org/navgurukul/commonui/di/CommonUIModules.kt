package org.navgurukul.commonui.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.navgurukul.commonui.resources.StringProvider

val factoryModule = module {
    single { StringProvider(androidContext().resources) }
}

val commonUIModules = arrayListOf( factoryModule)