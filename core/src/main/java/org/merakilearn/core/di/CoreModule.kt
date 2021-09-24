package org.merakilearn.core.di

import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import org.merakilearn.core.dynamic.module.DynamicFeatureModuleManager
import org.merakilearn.core.navigator.MerakiNavigator

private val module = module {
    single { MerakiNavigator(get(), get(),get(), get()) }
    single { DynamicFeatureModuleManager(SplitInstallManagerFactory.create(androidApplication())) }
}

val coreModules = arrayListOf(module)