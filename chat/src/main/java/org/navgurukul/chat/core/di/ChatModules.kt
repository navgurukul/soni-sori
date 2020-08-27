package org.navgurukul.chat.core.di

import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.api.MatrixConfiguration
import im.vector.matrix.android.api.session.room.model.RoomSummary
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.repo.*
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.core.resources.LocaleProvider
import org.navgurukul.chat.core.resources.StringProvider
import org.navgurukul.chat.core.utils.DataSource
import org.navgurukul.chat.features.home.room.list.ChaListtViewModel
import org.navgurukul.chat.features.grouplist.SelectedGroupDataSource
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.HomeRoomListDataSource
import org.navgurukul.chat.features.home.room.TypingHelper
import org.navgurukul.chat.features.home.room.format.DisplayableEventFormatter
import org.navgurukul.chat.features.home.room.format.NoticeEventFormatter
import org.navgurukul.chat.features.home.room.format.RoomHistoryVisibilityFormatter
import org.navgurukul.chat.features.home.room.list.ChronologicalRoomComparator
import org.navgurukul.chat.features.home.room.list.RoomSummaryItemFactory
import org.navgurukul.chat.features.notifications.NotificationUtils
import org.navgurukul.chat.features.settings.ChatPreferences

val viewModelModules = module {
    viewModel { ChaListtViewModel(get(), get(), get(), get()) }
}

val factoryModule = module {
    single {
        Matrix.initialize(androidContext(), MatrixConfiguration())
        Matrix.getInstance(androidContext()).authenticationService()
    }
    single { ActiveSessionHolder(get()) }
    single { AvatarRenderer(get()) }
    single { AuthenticationRepository(get(), get(), get(), get()) }
    single { AppStateHandler(get(), get(), get(), get()) }
    single { ActiveSessionDataSource() }
    single { HomeRoomListDataSource() }
    single { SelectedGroupDataSource() }
    single { ChronologicalRoomComparator() }
    single { RoomSummaryItemFactory(get(), get(), get()) }
    single { TypingHelper(get()) }
    single { SaralDateFormatter(androidContext(), get()) }
    single { DisplayableEventFormatter(get(), get(), get()) }
    single { NoticeEventFormatter(get(), get(), get()) }
    single { RoomHistoryVisibilityFormatter(get()) }
    single { NotificationUtils(androidContext(), get(), get()) }
    single { ChatPreferences(androidContext()) }
    single { StringProvider(androidContext().resources) }
    single { ColorProvider(androidContext()) }
    single { LocaleProvider(androidContext().resources) }
}

val chatModules = arrayListOf(viewModelModules, factoryModule)