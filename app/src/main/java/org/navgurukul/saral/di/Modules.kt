package org.navgurukul.saral.di

import android.app.Application
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.saral.datasource.network.SaralApi
import org.navgurukul.saral.datasource.ApplicationRepo
import org.navgurukul.saral.ui.home.HomeViewModel
import org.navgurukul.saral.ui.more.MoreViewModel
import org.navgurukul.saral.ui.onboarding.LoginViewModel
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MoreViewModel() }
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
        courseDb: CoursesDatabase
    ): ApplicationRepo {
        return ApplicationRepo(
            api,
            application,
            courseDb
        )
    }

    single { provideAppRepo(get(), androidApplication(),get()) }
}
val appModules = arrayListOf(viewModelModule,apiModule, repositoryModule)