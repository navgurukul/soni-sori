package org.navgurukul.learn.di

import android.app.Application
import android.view.ContextThemeWrapper
import androidx.room.Room
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.R
import org.navgurukul.learn.courses.db.*
import org.navgurukul.learn.courses.db.models.CourseClassContent
import org.navgurukul.learn.courses.db.typeadapters.Converters
import org.navgurukul.learn.courses.network.SaralCoursesApi
import org.navgurukul.learn.courses.repository.LearnRepo
import org.navgurukul.learn.ui.learn.*
import org.navgurukul.learn.util.ColorProvider
import org.navgurukul.learn.ui.learn.viewholder.AssessmentFragmentViewModel
import retrofit2.Retrofit

val viewModelModule = module {
    viewModel { LearnFragmentViewModel(get(), get(), get()) }
    viewModel { (args: CourseContentArgs) -> ExerciseFragmentViewModel(get(), get(), get(), args) }
    viewModel { (args: CourseContentArgs) -> ClassFragmentViewModel(get(), get(), get(), args) }
    viewModel { (args: CourseContentArgs) -> AssessmentFragmentViewModel(get(), get(), get(), args) }
    viewModel { (courseId: String, pathwayId: Int, contentId: String?) -> CourseContentActivityViewModel(get(), get(), get(), courseId, pathwayId, contentId) }
    viewModel { EnrollViewModel(get(), get(), get()) }
}


val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): SaralCoursesApi {
        return retrofit

            .create(SaralCoursesApi::class.java)
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
            .addMigrations(MIGRATION_5_6)
            .addMigrations(MIGRATION_6_7)
            .addMigrations(MIGRATION_7_8)
            .addMigrations(MIGRATION_8_9)
            .addMigrations(MIGRATION_9_10)
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

val factoryModule = module{
    single { ColorProvider(ContextThemeWrapper(androidContext(), R.style.AppTheme)) }
}

val learnModules = arrayListOf(
    viewModelModule, apiModule, databaseModule,
    repositoryModule, factoryModule
)
