package org.merakilearn.di

import android.content.Context
import androidx.preference.PreferenceManager
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
import org.merakilearn.InstallReferrerManager
import org.merakilearn.core.navigator.AppModuleNavigator
import org.merakilearn.datasource.ApplicationRepo
import org.merakilearn.datasource.ClassesRepo
import org.merakilearn.datasource.Config
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.navigation.AppModuleNavigationContract
import org.merakilearn.ui.home.HomeViewModel
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.ui.onboarding.WelcomeViewModel
import org.merakilearn.ui.profile.ProfileViewModel
import org.merakilearn.util.AppUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { WelcomeViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { (classId: Int, isEnrolled: Boolean) ->
        EnrollViewModel(
            classId = classId,
            isEnrolled = isEnrolled,
            stringProvider = get(),
            colorProvider = get(),
            classesRepo = get()
        )
    }
}

val factoryModule = module {
    single<AppModuleNavigator> { AppModuleNavigationContract() }
    single { InstallReferrerManager(androidApplication(), get()) }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralApi {
        return retrofit.create(SaralApi::class.java)
    }
    single { provideUserApi(get()) }
}


val networkModule = module {

    fun provideLogInterceptor(): Interceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }

    fun provideHttpClient(context: Context): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(provideLogInterceptor())
        okHttpClientBuilder.addInterceptor { chain ->
            val chainBuilder = chain.request().newBuilder()
            chainBuilder.addHeader("version-code", BuildConfig.VERSION_CODE.toString())
            chainBuilder.addHeader("platform", "android")
            chainBuilder.addHeader("Authorization", AppUtils.getAuthToken(context))
            chainBuilder.build().let(chain::proceed)
        }
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

    single { provideHttpClient(androidApplication()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }

}

val repositoryModule = module {
    single { ApplicationRepo(get(), androidApplication(), get(), get()) }
    single { Config() }
    single { ClassesRepo(get()) }
    single {
        UserRepo(
            get(),
            PreferenceManager.getDefaultSharedPreferences(androidApplication()),
            get(),
            get()
        )
    }
}

val appModules =
    arrayListOf(viewModelModule, apiModule, networkModule, factoryModule, repositoryModule)