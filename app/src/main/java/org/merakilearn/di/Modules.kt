package org.merakilearn.di

import android.content.Context
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
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
import org.merakilearn.datasource.*
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.navigation.AppModuleNavigationContract
import org.merakilearn.ui.home.HomeViewModel
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.ui.onboarding.WelcomeViewModel
import org.merakilearn.ui.profile.ProfileViewModel
import org.merakilearn.util.AppUtils
import org.navgurukul.learn.courses.db.models.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get()) }
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

    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(
                PolymorphicJsonAdapterFactory.of(ExerciseSlugDetail::class.java, "type")
                .withSubtype(ImageExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_IMAGE)
                .withSubtype(MarkDownExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_MD)
                .withSubtype(TypingExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_PRACTICE_TYPING)
                .withSubtype(PythonExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_PYTHON)
                .withSubtype(TypingExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_TRY_TYPING)
                .withSubtype(YoutubeExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_YOUTUBE_VIDEO)
            )
            .build()
    }


    fun provideRetrofit(factory: Moshi, client: OkHttpClient, settingsRepo: SettingsRepo): Retrofit {
        return Retrofit.Builder()
            .baseUrl(settingsRepo.serverBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single { provideHttpClient(androidApplication()) }
    single { provideMoshi() }
    single { provideRetrofit(get(), get(), get()) }

}

val repositoryModule = module {
    single { ApplicationRepo(get(), androidApplication(), get(), get()) }
    single { Config() }
    single { ClassesRepo(get()) }
    single { SettingsRepo(get()) }
    single {
        UserRepo(
            get(),
            PreferenceManager.getDefaultSharedPreferences(androidApplication()),
            get(),
            get(),
        )
    }
}

val appModules =
    arrayListOf(viewModelModule, apiModule, networkModule, factoryModule, repositoryModule)