package org.navgurukul.learn.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.courses.network.retrofit.RetrofitClient
import org.navgurukul.learn.courses.repository.CoursesRepositoryImpl
import org.navgurukul.learn.ui.learn.LearnViewModel

val viewModelModule = module {
    single { RetrofitClient }
}

val learnModules = arrayListOf(viewModelModule)