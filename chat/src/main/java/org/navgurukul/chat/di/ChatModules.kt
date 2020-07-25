package org.navgurukul.chat.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.chat.ui.chat.ChatViewModel

val viewModelModules = module {
    viewModel { ChatViewModel() }
}

val chatModules = arrayListOf(viewModelModules)