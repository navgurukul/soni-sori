package org.merakilearn.core.di

import android.view.ContextThemeWrapper
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.merakilearn.core.R
import org.merakilearn.core.additional.ActiveSessionDataSource
import org.merakilearn.core.additional.ActiveSessionHolder
import org.merakilearn.core.additional.AuthenticationRepository
import org.merakilearn.core.additional.AvatarRenderer
import org.merakilearn.core.additional.BitmapLoader
import org.merakilearn.core.additional.ChatPreferences
import org.merakilearn.core.additional.ColorProvider
import org.merakilearn.core.additional.DisplayableEventFormatter
import org.merakilearn.core.additional.IconLoader
import org.merakilearn.core.additional.ImageContentRenderer
import org.merakilearn.core.additional.ImageManager
import org.merakilearn.core.additional.KeyRequestHandler
import org.merakilearn.core.additional.NoticeEventFormatter
import org.merakilearn.core.additional.NotifiableEventResolver
import org.merakilearn.core.additional.NotificationDrawerManager
import org.merakilearn.core.additional.NotificationUtils
import org.merakilearn.core.additional.OutdatedEventDetector
import org.merakilearn.core.additional.PopupAlertManager
import org.merakilearn.core.additional.PushRuleTriggerListener
import org.merakilearn.core.additional.RoomHistoryVisibilityFormatter
import org.merakilearn.core.dynamic.module.DynamicFeatureModuleManager
import org.merakilearn.core.navigator.MerakiNavigator
import org.merakilearn.core.utils.CorePreferences

private val module = module {
    single { MerakiNavigator(get(), get(),get(), get()) }
    single { CorePreferences(get()) }
    single { DynamicFeatureModuleManager(SplitInstallManagerFactory.create(androidApplication())) }
}

val factoryModule = module {
    single {
        Matrix.initialize(androidContext(), MatrixConfiguration())
        Matrix.getInstance(androidContext()).authenticationService()
    }
    single { NotificationDrawerManager(androidContext(), get(), get(), get(), get(), get(), get(), get()) }
    single { IconLoader(androidContext()) }
    single { BitmapLoader(androidContext()) }
    single { OutdatedEventDetector(get()) }
    single { PushRuleTriggerListener(get()) }
    single { NotifiableEventResolver(get(), get(), get()) }
    single { ActiveSessionHolder(get(), get(), get(), get()) }
    single { KeyRequestHandler(androidContext(), get()) }
    single { PopupAlertManager() }
    single { ImageManager(androidContext(), get()) }
    single { AvatarRenderer(get()) }
    single { AuthenticationRepository(get(), get(), get(), get(), androidContext()) }
    single { ActiveSessionDataSource() }
    single { DisplayableEventFormatter(get(), get(), get()) }
    single { NoticeEventFormatter(get(), get(), get()) }
    single { RoomHistoryVisibilityFormatter(get()) }
    single { NotificationUtils(androidContext(), get(), get(), get()) }
    single { ChatPreferences(androidContext()) }
    single { ColorProvider(ContextThemeWrapper(androidContext(), R.style.AppTheme)) }
    single { ImageContentRenderer(get(), get()) }
}

val coreModules = arrayListOf(module, factoryModule)