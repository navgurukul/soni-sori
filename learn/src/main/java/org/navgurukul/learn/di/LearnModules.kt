package org.navgurukul.learn.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.courses.db.CoursesDatabase
import org.navgurukul.learn.courses.db.MIGRATION_1_2
import org.navgurukul.learn.courses.db.MIGRATION_2_3
import org.navgurukul.learn.courses.db.typeadapters.Converters
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.LearnFragmentViewModel
import org.navgurukul.learn.ui.learn.LearnViewModel
import org.navgurukul.learn.util.LearnPreferences
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LearnViewModel(get(), get()) }
    viewModel { LearnFragmentViewModel(get(), get(), get()) }
}


val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralCoursesApi {
        return retrofit.create(SaralCoursesApi::class.java)
    }
    single { provideUserApi(get()) }
}

val databaseModule = module {

    fun provideDatabase(application: Application, moshi: Moshi): CoursesDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            CoursesDatabase::class.java,
            "course.db"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_2_3)
            .addTypeConverter(Converters(moshi))
            .build()
    }

    single { provideDatabase(androidApplication(), get()) }
    single { LearnPreferences(androidApplication()) }
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