package org.navgurukul.chat.core.di

import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.api.MatrixConfiguration
import im.vector.matrix.android.api.auth.AuthenticationService
import im.vector.matrix.android.internal.auth.DefaultAuthenticationService_Factory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.chat.core.repo.ActiveSessionDataSource
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.repo.AppStateHandler
import org.navgurukul.chat.core.repo.ChatRepository
import org.navgurukul.chat.features.chat.ChatViewModel
import org.navgurukul.chat.features.grouplist.SelectedGroupDataSource
import org.navgurukul.chat.features.home.HomeRoomListDataSource
import org.navgurukul.chat.features.home.room.list.ChronologicalRoomComparator

val viewModelModules = module {
    viewModel { ChatViewModel(get()) }
}

val repoModule = module {
    single { ChatRepository(get(), get(), androidContext(), get()) }
}

val factoryModule = module {
    single {
        Matrix.initialize(androidContext(), MatrixConfiguration())
        Matrix.getInstance(androidContext()).authenticationService()
    }
    single { ActiveSessionHolder(get(), get()) }
    single { AppStateHandler(get(), get(), get(), get()) }
    single { ActiveSessionDataSource() }
    single { HomeRoomListDataSource() }
    single { SelectedGroupDataSource() }
    single { ChronologicalRoomComparator() }
}

val chatModules = arrayListOf(viewModelModules, repoModule, factoryModule)