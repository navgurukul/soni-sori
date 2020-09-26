package org.navgurukul.chat.core.di

import android.view.ContextThemeWrapper
import im.vector.matrix.android.api.Matrix
import im.vector.matrix.android.api.MatrixConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.navgurukul.chat.EmojiCompatFontProvider
import org.navgurukul.chat.R
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.error.ChatErrorFormatter
import org.navgurukul.chat.core.repo.*
import org.navgurukul.chat.core.resources.*
import org.navgurukul.chat.core.utils.DimensionConverter
import org.navgurukul.chat.features.crypto.KeyRequestHandler
import org.navgurukul.chat.features.grouplist.SelectedGroupDataSource
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.HomeRoomListDataSource
import org.navgurukul.chat.features.home.room.TypingHelper
import org.navgurukul.chat.features.home.room.detail.*
import org.navgurukul.chat.features.home.room.detail.timeline.MessageColorProvider
import org.navgurukul.chat.features.home.room.detail.timeline.TimelineEventController
import org.navgurukul.chat.features.home.room.detail.timeline.factory.*
import org.navgurukul.chat.features.home.room.detail.timeline.helper.*
import org.navgurukul.chat.features.home.room.format.DisplayableEventFormatter
import org.navgurukul.chat.features.home.room.format.NoticeEventFormatter
import org.navgurukul.chat.features.home.room.format.RoomHistoryVisibilityFormatter
import org.navgurukul.chat.features.home.room.list.*
import org.navgurukul.chat.features.html.EventHtmlRenderer
import org.navgurukul.chat.features.html.MatrixHtmlPluginConfigure
import org.navgurukul.chat.features.html.SaralHtmlCompressor
import org.navgurukul.chat.features.media.ImageContentRenderer
import org.navgurukul.chat.features.navigator.ChatNavigator
import org.navgurukul.chat.features.navigator.DefaultChatNavigator
import org.navgurukul.chat.features.notifications.NotificationUtils
import org.navgurukul.chat.features.popup.PopupAlertManager
import org.navgurukul.chat.features.settings.ChatPreferences
import org.navgurukul.commonui.error.ErrorFormatter

val viewModelModules = module {
    viewModel { (roomListViewState : RoomListViewState) -> RoomListViewModel(roomListViewState, get(), get(), get(), get()) }
    viewModel { (roomId : String) -> RoomDetailViewModel(roomId, get(), get()) }
    viewModel { (roomDetailViewState : RoomDetailViewState) -> RoomDetailFragmentViewModel(roomDetailViewState, get(), get(), get(), get()) }
}

val factoryModule = module {
    single {
        Matrix.initialize(androidContext(), MatrixConfiguration())
        Matrix.getInstance(androidContext()).authenticationService()
    }
    single { UserPreferencesProvider(get()) }
    single { ActiveSessionHolder(get(), get(), get()) }
    single { KeyRequestHandler(androidContext(), get()) }
    single { PopupAlertManager() }
    single { ImageManager(androidContext(), get()) }
    single { AvatarRenderer(get()) }
    single { AuthenticationRepository(get(), get(), get(), get()) }
    single { AppStateHandler(get(), get(), get(), get()) }
    single { ActiveSessionDataSource() }
    single { HomeRoomListDataSource() }
    single { SelectedGroupDataSource() }
    single { ChronologicalRoomComparator() }

    single { TypingHelper(get()) }
    single { SaralDateFormatter(androidContext(), get()) }
    single { DisplayableEventFormatter(get(), get(), get()) }
    single { NoticeEventFormatter(get(), get(), get()) }
    single { RoomHistoryVisibilityFormatter(get()) }
    single { NotificationUtils(androidContext(), get(), get()) }
    single { ChatPreferences(androidContext()) }
    single { StringProvider(androidContext().resources) }
    single { ColorProvider(ContextThemeWrapper(androidContext(), R.style.AppTheme)) }
    single { DimensionConverter(androidContext().resources) }
    single { LocaleProvider(androidContext().resources) }
    single<ChatNavigator> { DefaultChatNavigator(get(), get()) }
    single { SaralHtmlCompressor() }
    single { MessageColorProvider(get()) }
    single { AvatarSizeProvider(get()) }
    single { DrawableProvider(get()) }
    single { ImageContentRenderer(get(), get()) }
    single { EventHtmlRenderer(androidContext(), get()) }
    single { MatrixHtmlPluginConfigure(androidContext(), get(), get(), get()) }
    single { EmojiCompatFontProvider() }
    single<ErrorFormatter> { ChatErrorFormatter(get()) }

    factory { RoomSummaryController(get()) }
    factory { RoomSummaryItemFactory(get(), get(), get(), get()) }

    factory { (scope : Scope) -> NoticeItemFactory(informationDataFactory = get(parameters = {parametersOf(scope)}),
        eventFormatter = get(),
        avatarRenderer = get(),
        avatarSizeProvider = get()) }

    factory { (scope : Scope) -> MessageInformationDataFactory(sessionHolder = get(),
        roomSummaryHolder = scope.get(),
        dateFormatter = get()) }

    factory { (scope : Scope) -> DefaultItemFactory(informationDataFactory = get(parameters = {parametersOf(scope)}),
        avatarSizeProvider = get(),
        avatarRenderer = get(),
        stringProvider = get()) }

    factory { (scope : Scope) -> VerificationItemFactory(messageColorProvider = get(),
        messageItemAttributesFactory = get(),
        avatarSizeProvider = get(),
        userPreferencesProvider = get(),
        stringProvider = get(),
        sessionHolder = get(),
        messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
        noticeItemFactory = get(parameters = {parametersOf(scope)})) }

    factory { (scope : Scope) -> RoomCreateItemFactory(noticeItemFactory = get(parameters = {parametersOf(scope)}),
        stringProvider = get(),
        userPreferencesProvider = get()) }

    factory { (scope : Scope) -> EncryptedItemFactory(messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
        colorProvider = get(),
        stringProvider =  get(),
        avatarSizeProvider = get(),
        drawableProvider = get(),
        attributesFactory = get(),
        chatPreferences = get()) }

    factory { (scope : Scope) -> EncryptionItemFactory(informationDataFactory = get(parameters = {parametersOf(scope)}),
        messageItemAttributesFactory = get(),
        messageColorProvider = get(),
        stringProvider = get(),
        avatarSizeProvider = get()) }

    factory { (scope : Scope) -> MessageItemFactory(colorProvider = get(),
        dimensionConverter = get(),
        htmlCompressor = get(),
        stringProvider = get(),
        imageContentRenderer = get(),
        messageItemAttributesFactory = get(),
        avatarSizeProvider = get(),
        sessionHolder = get(),
        htmlRenderer = get(),
        timelineMediaSizeProvider = scope.get(),
        contentUploadStateTrackerBinder = scope.get(),
        contentDownloadStateTrackerBinder = scope.get(),
        messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
        defaultItemFactory = get(parameters = {parametersOf(scope)}),
        noticeItemFactory = get(parameters = {parametersOf(scope)})) }

    factory { (scope : Scope) -> TimelineItemFactory( messageItemFactory = get(parameters = {parametersOf(scope)}),
        encryptedItemFactory = get(parameters = {parametersOf(scope)}),
        encryptionItemFactory = get(parameters = {parametersOf(scope)}),
        noticeItemFactory = get(parameters = {parametersOf(scope)}),
        defaultItemFactory = get(parameters = {parametersOf(scope)}),
        roomCreateItemFactory = get(parameters = {parametersOf(scope)}),
        verificationConclusionItemFactory = get(parameters = {parametersOf(scope)}),
        userPreferencesProvider = get()) }

    factory { (scope : Scope) -> TimelineEventController(contentUploadStateTrackerBinder = scope.get(),
        contentDownloadStateTrackerBinder = scope.get(),
        timelineMediaSizeProvider = scope.get(),
        timelineItemFactory = get(parameters = {parametersOf(scope)}),
        dateFormatter = get(),
        mergedHeaderItemFactory = get(),
        backgroundHandler = TimelineAsyncHelper.getBackgroundHandler()) }

    factory { MergedHeaderItemFactory(avatarSizeProvider = get(),
        avatarRenderer = get(),
        activeSessionHolder =  get(),
        chatPreferences =  get()) }

    factory { MessageItemAttributesFactory(avatarRenderer = get(),
        messageColorProvider =  get(),
        avatarSizeProvider = get(),
        emojiCompatFontProvider = get()) }

    scope(named<RoomDetailFragment>()) {
        scoped { TimelineMediaSizeProvider() }
        scoped { ContentUploadStateTrackerBinder(get(), get(), get()) }
        scoped { ContentDownloadStateTrackerBinder(get(), get(), get()) }
        scoped { RoomSummaryHolder() }
    }
}

val chatModules = arrayListOf(viewModelModules, factoryModule)