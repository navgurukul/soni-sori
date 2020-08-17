package org.navgurukul.learn.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.courses.db.CourseDao
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.ExerciseDao
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.LearnViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://saral.navgurukul.org/"

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
        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().create()
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

val databaseModule = module {

    fun provideDatabase(application: Application): CoursesDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            CoursesDatabase::class.java,
            "course.db"
        ).build()
    }

    fun provideCourseDao(database: CoursesDatabase): CourseDao {
        return database.courseDao()
    }

    fun provideExerciseDao(database: CoursesDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideCourseDao(get()) }
    single { provideExerciseDao(get()) }
}

val repositoryModule = module {
    fun provideLearnRepository(
        api: SaralCoursesApi,
        courseDao: CourseDao,
        exerciseDao: ExerciseDao
    ): LearnRepo {
        return LearnRepo(
            api,
            courseDao,
            exerciseDao
        )
    }

    single { provideLearnRepository(get(), get(), get()) }
}

val learnModules = arrayListOf(viewModelModule, apiModule, netModule, databaseModule,
    repositoryModule)