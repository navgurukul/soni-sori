package org.navgurukul.saral.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.saral.ui.more.MoreViewModel

val viewModelModule = module {
    viewModel { MoreViewModel() }
}

val appModules = arrayListOf(viewModelModule)