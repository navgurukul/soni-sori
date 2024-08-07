package org.merakilearn.core.di

import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.merakilearn.core.appopen.AppOpenDelegate
import org.merakilearn.core.dynamic.module.DynamicFeatureModuleManager
import org.merakilearn.core.impl.AppOpenDelegateImpl
import org.merakilearn.core.impl.FCMServiceDelegateImpl
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.push.FCMServiceDelegate
import org.merakilearn.core.utils.CorePreferences

private val module = module {
    single { MerakiNavigator(get(), get(),get(), get()) }
    single { CorePreferences(get()) }
    single { DynamicFeatureModuleManager(SplitInstallManagerFactory.create(androidApplication())) }
    single<FCMServiceDelegate> { FCMServiceDelegateImpl() }
    single<AppOpenDelegate> { AppOpenDelegateImpl() }
}

val coreModules = arrayListOf(module)