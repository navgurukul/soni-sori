package org.navgurukul.playground.di

import android.app.Application
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.merakilearn.core.navigator.PlaygroundModuleNavigator
import org.navgurukul.playground.navigation.PythonModuleNavigatorImpl
import org.navgurukul.playground.repo.PythonRepository
import org.navgurukul.playground.repo.PythonRepositoryImpl
import org.navgurukul.playground.editor.PythonEditorArgs
import org.navgurukul.playground.editor.PythonEditorViewModel

val viewModelModules = module {
    viewModel { (args: PythonEditorArgs, errorColor: Int) -> PythonEditorViewModel(args, get(), get(), errorColor) }
}

val repoModules = module {
    fun provideSharedPrefs(androidApplication: Application): SharedPreferences {
        return androidApplication.getSharedPreferences(
            "default",
            android.content.Context.MODE_PRIVATE
        )
    }

    single { provideSharedPrefs(androidApplication()) }

    fun providePlaygroundRepository(sharedPreferences: SharedPreferences, application: Application): PythonRepository {
        return PythonRepositoryImpl(sharedPreferences,application)
    }

    single { providePlaygroundRepository(get(),androidApplication()) }
}

val factoryModule = module {
    single<PlaygroundModuleNavigator> { PythonModuleNavigatorImpl() }
}
val playgroundModules = arrayListOf(viewModelModules, repoModules,factoryModule)