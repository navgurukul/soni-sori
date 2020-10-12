package org.navgurukul.learn.di

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.LearnViewModel
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LearnViewModel(get()) }
}


val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralCoursesApi {
        return retrofit.create(SaralCoursesApi::class.java)
    }
    single { provideUserApi(get()) }
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
    viewModelModule, apiModule, databaseModule,
    repositoryModule
)