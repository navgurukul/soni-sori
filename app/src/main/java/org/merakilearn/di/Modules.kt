package org.merakilearn.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.merakilearn.BuildConfig
import org.merakilearn.EnrollViewModel
import org.merakilearn.core.navigator.AppModuleNavigator
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.Config
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.navigation.AppModuleNavigationContract
import org.merakilearn.ui.discover.DiscoverViewModel
import org.merakilearn.ui.home.HomeViewModel
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.ui.onboarding.WelcomeViewModel
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.learn.courses.db.CoursesDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { WelcomeViewModel(get(), get(), get()) }
    viewModel { DiscoverViewModel(get(), get(), get()) }
    viewModel { (classId : Int, isEnrolled: Boolean) -> EnrollViewModel(classId = classId,
        isEnrolled = isEnrolled,
        stringProvider = get(),
        applicationRepo = get()) }
}

val factoryModule = module {
    single<AppModuleNavigator> { AppModuleNavigationContract() }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralApi {
        return retrofit.create(SaralApi::class.java)
    }
    single { provideUserApi(get()) }
}


val networkModule = module {

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(provideLogInterceptor())
        okHttpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.readTimeout(5, TimeUnit.MINUTES)

        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").setLenient().create()
    }


    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single { provideHttpClient() }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }

}

fun provideLogInterceptor(): Interceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = if (BuildConfig.DEBUG)
        HttpLoggingInterceptor.Level.BODY
    else
        HttpLoggingInterceptor.Level.NONE
    return logging
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

    single { provideAppRepo(get(), androidApplication(), get(), get()) }
    single { Config() }
}
val appModules = arrayListOf(viewModelModule, apiModule, networkModule, factoryModule, repositoryModule)