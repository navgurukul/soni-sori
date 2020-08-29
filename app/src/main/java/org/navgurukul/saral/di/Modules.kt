package org.navgurukul.saral.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.network.SaralLoginApi
import org.navgurukul.learn.di.BASE_URL
import org.navgurukul.saral.datasource.LoginRepo
import org.navgurukul.saral.ui.more.MoreViewModel
import org.navgurukul.saral.ui.onboarding.LoginViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel {
        MoreViewModel()
        LoginViewModel(get())
    }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralLoginApi {
        return retrofit.create(SaralLoginApi::class.java)
    }
    single { provideUserApi(get()) }
}

val repositoryModule = module {
    fun provideLoginRepo(
        api: SaralLoginApi,
        application: Application
    ): LoginRepo {
        return LoginRepo(
            api,
            application
        )
    }

    single { provideLoginRepo(get(), androidApplication()) }
}
val appModules = arrayListOf(viewModelModule,apiModule, repositoryModule)