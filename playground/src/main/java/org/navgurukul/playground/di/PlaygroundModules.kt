package org.navgurukul.playground.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.playground.ui.playground.PlaygroundViewModel

val viewModelModules = module {
    viewModel { PlaygroundViewModel() }
}

val playgroundModules = arrayListOf(viewModelModules)