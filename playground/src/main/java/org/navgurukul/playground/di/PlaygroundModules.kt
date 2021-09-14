package org.navgurukul.playground.di

import android.app.Application
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.merakilearn.core.navigator.PlaygroundModuleNavigator
import org.navgurukul.playground.navigation.PlaygroundModuleNavigatorImpl
import org.navgurukul.playground.repo.PlaygroundRepository
import org.navgurukul.playground.repo.PlaygroundRepositoryImpl
import org.navgurukul.playground.ui.PlaygroundViewModel
import org.navgurukul.playground.ui.PythonPlaygroundViewModel

val viewModelModules = module {
    viewModel { PythonPlaygroundViewModel(get()) }
    viewModel { PlaygroundViewModel(get()) }
}

val repoModules = module {
    fun provideSharedPrefs(androidApplication: Application): SharedPreferences {
        return androidApplication.getSharedPreferences(
            "default",
            android.content.Context.MODE_PRIVATE
        )
    }

    single { provideSharedPrefs(androidApplication()) }

    fun providePlaygroundRepository(sharedPreferences: SharedPreferences, application: Application): PlaygroundRepository {
        return PlaygroundRepositoryImpl(sharedPreferences,application)
    }

    single { providePlaygroundRepository(get(),androidApplication()) }
}

val factoryModule = module {
    single<PlaygroundModuleNavigator> { PlaygroundModuleNavigatorImpl() }
}
val playgroundModules = arrayListOf(viewModelModules, repoModules,factoryModule)