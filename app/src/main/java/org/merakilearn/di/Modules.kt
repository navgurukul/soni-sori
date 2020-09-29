package org.merakilearn.di

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.ui.home.HomeViewModel
import org.merakilearn.ui.more.MoreViewModel
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.ui.onboarding.WelcomeViewModel
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.chat.core.repo.AuthenticationRepository
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MoreViewModel() }
    viewModel { WelcomeViewModel(get(), get(), get()) }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralApi {
        return retrofit.create(SaralApi::class.java)
    }
    single { provideUserApi(get()) }
}

val repositoryModule = module {
    fun provideAppRepo(
        api: SaralApi,
        application: Application,
        courseDb: CoursesDatabase,
        authenticationRepository: AuthenticationRepository
    ): ApplicationRepo {
        return ApplicationRepo(
            api,
            application,
            courseDb,
            authenticationRepository
        )
    }

    single { provideAppRepo(get(), androidApplication(),get(), get()) }
}
val appModules = arrayListOf(viewModelModule,apiModule, repositoryModule)