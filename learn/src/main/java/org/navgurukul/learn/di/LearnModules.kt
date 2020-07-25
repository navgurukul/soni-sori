package org.navgurukul.learn.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.learn.ui.learn.LearnViewModel

val viewModelModule = module {
    viewModel { LearnViewModel() }
}

val learnModules = arrayListOf(viewModelModule)