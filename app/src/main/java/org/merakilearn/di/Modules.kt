package org.merakilearn.di

import android.app.Application
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
import org.koin.java.KoinJavaComponent.get
import org.merakilearn.BuildConfig
import org.merakilearn.EnrollViewModel
import org.merakilearn.InstallReferrerManager
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.navigator.AppModuleNavigator
import org.merakilearn.datasource.*
import org.merakilearn.datasource.network.SaralApi
import org.merakilearn.navigation.AppModuleNavigationContract
import org.merakilearn.repo.ScratchRepository
import org.merakilearn.repo.ScratchRepositoryImpl
import org.merakilearn.ui.onboarding.LoginViewModel
import org.merakilearn.ui.onboarding.OnBoardingActivityArgs
import org.merakilearn.ui.onboarding.OnBoardingPagesViewModel
import org.merakilearn.ui.onboarding.OnBoardingViewModel
import org.merakilearn.ui.playground.PlaygroundViewModel
import org.merakilearn.ui.profile.ProfileViewModel
import org.navgurukul.learn.courses.db.models.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { OnBoardingPagesViewModel(get(), get(), get(), get(), get()) }
    viewModel { (args: OnBoardingActivityArgs?) -> OnBoardingViewModel(args, get(), get(), get(),get()) }
    viewModel { PlaygroundViewModel( get(),get(), get()) }
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

    fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(provideLogInterceptor())
        okHttpClientBuilder.addInterceptor { chain ->
            val chainBuilder = chain.request().newBuilder()
            val userRepo: UserRepo = get(UserRepo::class.java)
            chainBuilder.addHeader("version-code", BuildConfig.VERSION_CODE.toString())
            chainBuilder.addHeader("platform", "android")
            chainBuilder.addHeader("Authorization", userRepo.getAuthToken())
            chainBuilder.build().let(chain::proceed)
        }
        okHttpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.readTimeout(5, TimeUnit.MINUTES)
        okHttpClientBuilder.writeTimeout(5, TimeUnit.MINUTES)

        return okHttpClientBuilder.build()
    }

    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(
                PolymorphicJsonAdapterFactory.of(BaseCourseContent::class.java, "component")
                    .withSubtype(
                        ImageBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_IMAGE
                    )
                    .withSubtype(
                        TextBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_TEXT
                    )
                    .withSubtype(
                        LinkBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_LINK
                    )
//                .withSubtype(CodeExerciseSlugDetail::class.java, ExerciseSlugDetail.TYPE_SOLUTION)
                    .withSubtype(
                        CodeBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_CODE
                    )
                    .withSubtype(
                        QuestionCodeBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_QUESTION_CODE
                    )
                    .withSubtype(
                        QuestionExpressionBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_QUESTION_EXPRESSION
                    )
                    .withSubtype(
                        BlockQuoteBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_BLOCK_QUOTE
                    )
                    .withSubtype(
                        HeaderBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_HEADER
                    )
                    .withSubtype(
                        TableBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_TABLE
                    )
                    .withSubtype(
                        BannerBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_BANNER
                    )
                    .withSubtype(
                        SolutionBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_SOLUTION
                    )
                    .withSubtype(
                        OptionsBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_OPTIONS
                    )
                    .withSubtype(
                        OutputBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_OUTPUT
                    )
                    .withSubtype(
                        YoutubeBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_YOUTUBE_VIDEO
                    )
                    .withSubtype(
                        UnknownBaseCourseContent::class.java,
                        BaseCourseContent.COMPONENT_UNKNOWN
                    )
                    .withDefaultValue(UnknownBaseCourseContent())
            )
            .add(
                PolymorphicJsonAdapterFactory.of(CourseContents::class.java, "content_type")
                    .withSubtype(CourseExerciseContent::class.java, CourseContentType.exercise.name)
                    .withSubtype(CourseClassContent::class.java, CourseContentType.class_topic.name)
                    .withSubtype(
                        CourseAssessmentContent::class.java,
                        CourseContentType.assessment.name
                    )
            )
            .build()
    }


    fun provideRetrofit(
        factory: Moshi,
        client: OkHttpClient,
        settingsRepo: SettingsRepo
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(settingsRepo.serverBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single { provideHttpClient() }
    single { provideMoshi() }
    single { provideRetrofit(get(), get(), get()) }

}

val repositoryModule = module {
    single { LoginRepository(get(), androidApplication(), get(), get(), get(), get()) }
    single { Config() }
    single { ClassesRepo(get()) }
    single { SettingsRepo(get()) }
    single { PlaygroundRepo() }
    single {
        UserRepo(
            get(),
            PreferenceManager.getDefaultSharedPreferences(androidApplication()),
            get(),
            get(),
        )
    }

    fun providePlaygroundRepository(application: Application): ScratchRepository {
        return ScratchRepositoryImpl(application)
    }

    single { providePlaygroundRepository(androidApplication()) }
}

val appModules =
    arrayListOf(viewModelModule, apiModule, networkModule, factoryModule, repositoryModule)