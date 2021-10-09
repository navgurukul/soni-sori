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
import org.navgurukul.learn.courses.db.MIGRATION_3_4
import org.navgurukul.learn.courses.db.MIGRATION_4_5
import org.navgurukul.learn.courses.db.models.CurrentStudy
import org.navgurukul.learn.courses.db.typeadapters.Converters
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.ExerciseActivityViewModel
import org.navgurukul.learn.ui.learn.ExerciseFragmentViewModel
import org.navgurukul.learn.ui.learn.LearnFragmentViewModel
import org.navgurukul.learn.ui.learn.LearnViewModel
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LearnViewModel(get(), get()) }
    viewModel { LearnFragmentViewModel(get(), get()) }
    viewModel { (courseId: String, exerciseId: String) -> ExerciseFragmentViewModel(get(), get(), get(),
    courseId, exerciseId) }
    viewModel { (courseId: String, currentStudy: CurrentStudy?) -> ExerciseActivityViewModel(get(), get(), get(), courseId, currentStudy) }
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
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_4_5)
            .addTypeConverter(Converters(moshi))
            .build()
    }

    single { provideDatabase(androidApplication(), get()) }
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