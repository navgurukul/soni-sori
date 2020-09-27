package org.navgurukul.learn.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.BuildConfig
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.LearnViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://dev-api.merakilearn.org/apiDocs/"

val viewModelModule = module {
    viewModel { LearnViewModel(get()) }
}


val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralCoursesApi {
        return retrofit.create(SaralCoursesApi::class.java)
    }
    single { provideUserApi(get()) }
}

val netModule = module {

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(provideLogInterceptor())
        okHttpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.readTimeout(5, TimeUnit.MINUTES)

        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }


    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
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

val databaseModule = module {

    fun provideDatabase(application: Application): CoursesDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            CoursesDatabase::class.java,
            "course.db"
        ).build()
    }

    single { provideDatabase(androidApplication()) }
}

val repositoryModule = module {
    fun provideLearnRepository(
        api: SaralCoursesApi,
        application: Application,
        database: CoursesDatabase
    ): LearnRepo {
        return LearnRepo(
            api,
            application,
            database
        )
    }

    single { provideLearnRepository(get(), androidApplication(), get()) }
}

val learnModules = arrayListOf(
    viewModelModule, apiModule, netModule, databaseModule,
    repositoryModule
)