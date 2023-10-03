package org.navgurukul.chat.core.di

import android.view.ContextThemeWrapper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.navgurukul.chat.R
import org.navgurukul.chat.core.repo.ActiveSessionDataSource
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.core.repo.AuthenticationRepository
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.features.crypto.KeyRequestHandler
import org.navgurukul.chat.features.home.room.format.DisplayableEventFormatter
import org.navgurukul.chat.features.home.room.format.NoticeEventFormatter
import org.navgurukul.chat.features.home.room.format.RoomHistoryVisibilityFormatter
import org.navgurukul.chat.features.notifications.*
import org.navgurukul.chat.features.settings.ChatPreferences

val viewModelModules = module {
//    viewModel { EmojiSearchResultViewModel(EmojiSearchResultViewState(), get()) }
//    viewModel { EmojiChooserViewModel() }
//    viewModel { RoomProfileSharedActionViewModel() }
//    viewModel { RoomListQuickActionsSharedActionViewModel() }
//    viewModel { (displayReactionsViewState : DisplayReactionsViewState) -> ViewReactionsViewModel(displayReactionsViewState, get(), get()) }
//    viewModel { (roomMemberListViewState : RoomMemberListViewState) -> RoomMemberListViewModel(roomMemberListViewState, RoomMemberSummaryComparator(), get()) }
//    viewModel { (roomListQuickActionsState : RoomListQuickActionsState) -> RoomListQuickActionsViewModel(roomListQuickActionsState, get()) }
//    viewModel { (roomId : String) -> RequireActiveMembershipViewModel(roomId, get(), get()) }
//    viewModel { (viewState : RoomProfileViewState) -> RoomProfileViewModel(viewState, get(), get()) }
//    viewModel { (messageActionState : MessageActionState) -> MessageActionsViewModel(messageActionState, get(), get(), get(), get(),get()) }
//    viewModel { (roomDetailViewState : RoomDetailViewState, scope: Scope) -> RoomDetailFragmentViewModel(
//        initialState = roomDetailViewState,
////        userPreferencesProvider = get(),
//        chatPreferences = get(),
//        stringProvider = get(),
//        sessionHolder = get(),
//        roomSummaryHolder = scope.get(),
//        typingHelper = get()) }
}

val factoryModule = module {
    single {
        Matrix.initialize(androidContext(), MatrixConfiguration())
        Matrix.getInstance(androidContext()).authenticationService()
    }
    single { NotificationDrawerManager(androidContext(), get(), get(), get(), get(), get(), get(), get()) }
    single { IconLoader(androidContext()) }
    single { BitmapLoader(androidContext()) }
    single { OutdatedEventDetector(get()) }
//    single<FCMServiceDelegate> { ChatFCMServiceDelegate(androidContext()) }
//    single<AppOpenDelegate> { ChatAppOpenDelegate(get(), get(), get(), get()) }
//    single { FcmHelper() }
//    single { PushersManager(get(), get(), get()) }
//    single { UserPreferencesProvider(get()) }
    single { PushRuleTriggerListener(get()) }
    single { NotifiableEventResolver(get(), get(), get()) }
    single { ActiveSessionHolder(get(), get(), get(), get()) }
    single { KeyRequestHandler(androidContext(), get()) }
//    single { PopupAlertManager() }
    single { ImageManager(androidContext(), get()) }
//    single { AvatarRenderer(get()) }
    single { AuthenticationRepository(get(), get(), get(), get(), androidContext()) }
//    single { AppStateHandler(get(), get(), get(), get()) }
    single { ActiveSessionDataSource() }
//    single { HomeRoomListDataSource() }
//    single { SelectedGroupDataSource() }
//    single { ChronologicalRoomComparator() }

//    single { TypingHelper(get()) }
//    single { SaralDateFormatter(androidContext(), get()) }
    single { DisplayableEventFormatter(get(), get(), get()) }
    single { NoticeEventFormatter(get(), get(), get()) }
    single { RoomHistoryVisibilityFormatter(get()) }
    single { NotificationUtils(androidContext(), get(), get(), get()) }
    single { ChatPreferences(androidContext()) }
    single { ColorProvider(ContextThemeWrapper(androidContext(), R.style.AppTheme)) }
//    single { DimensionConverter(androidContext().resources) }
//    single { LocaleProvider(androidContext().resources) }
//    single<ChatModuleNavigator> { ChatNavigatorContract(get(), get()) }
//    single { ChatInternalNavigator() }
//    single { AttachmentProviderFactory(get(), get(), get()) }
//    single { SaralHtmlCompressor() }
//    single { MessageColorProvider(get()) }
//    single { AvatarSizeProvider(get()) }
//    single { DrawableProvider(get()) }
//    single { ImageContentRenderer(get(), get()) }
//    single { EventHtmlRenderer(androidContext(), get()) }
//    single { MatrixHtmlPluginConfigure(androidContext(), get(), get(), get()) }
//    single { EmojiCompatFontProvider() }
//    single<ErrorFormatter> { ChatErrorFormatter(get()) }
//    single { EmojiDataSource(androidContext().resources) }
//    single { EmojiCompatWrapper(androidContext()) }

//    factory { RoomProfileController(get(), get()) }
//    factory { RoomListQuickActionsEpoxyController(get(), get()) }
//    factory { RoomMemberListController(get(), get(), RoomMemberSummaryFilter(), get()) }
//    factory { EmojiSearchResultController(get(), get()) }
//    factory { EmojiRecyclerAdapter(get()) }
//
//    factory { ViewReactionsEpoxyController(get(), get()) }
//
//    factory { RoomSummaryController(get()) }
//    factory { RoomSummaryItemFactory(get(), get(), get(), get()) }
//
//    factory { (scope : Scope) -> NoticeItemFactory(informationDataFactory = get(parameters = {parametersOf(scope)}),
//        eventFormatter = get(),
//        avatarRenderer = get(),
//        avatarSizeProvider = get()) }

//    factory { (scope : Scope) -> MessageInformationDataFactory(sessionHolder = get(),
//        roomSummaryHolder = scope.get(),
//        dateFormatter = get()) }

//    factory { (scope : Scope) -> DefaultItemFactory(informationDataFactory = get(parameters = {parametersOf(scope)}),
//        avatarSizeProvider = get(),
//        avatarRenderer = get(),
//        stringProvider = get()) }
//
//    factory { (scope : Scope) -> VerificationItemFactory(
//        messageColorProvider = get(),
//        messageItemAttributesFactory = get(),
//        userPreferencesProvider = get(),
//        stringProvider = get(),
//        sessionHolder = get(),
//        messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
//        noticeItemFactory = get(parameters = {parametersOf(scope)})
//    ) }

//    factory { (scope : Scope) -> RoomCreateItemFactory(noticeItemFactory = get(parameters = {parametersOf(scope)}),
//        stringProvider = get(),
//        userPreferencesProvider = get()) }
//
//    factory { (scope : Scope) -> EncryptedItemFactory(messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
//        colorProvider = get(),
//        stringProvider =  get(),
//        avatarSizeProvider = get(),
//        drawableProvider = get(),
//        attributesFactory = get(),
//        chatPreferences = get()) }

//    factory { (scope : Scope) -> EncryptionItemFactory(
//        messageItemAttributesFactory = get(),
//        messageColorProvider = get(),
//        stringProvider = get(),
//        informationDataFactory = get(parameters = {parametersOf(scope)})
//    ) }
//
//    factory { (scope : Scope) -> MessageItemFactory(colorProvider = get(),
//        dimensionConverter = get(),
//        htmlCompressor = get(),
//        stringProvider = get(),
//        imageContentRenderer = get(),
//        messageItemAttributesFactory = get(),
//        avatarSizeProvider = get(),
//        sessionHolder = get(),
//        htmlRenderer = get(),
//        timelineMediaSizeProvider = scope.get(),
//        contentUploadStateTrackerBinder = scope.get(),
//        contentDownloadStateTrackerBinder = scope.get(),
//        messageInformationDataFactory = get(parameters = {parametersOf(scope)}),
//        defaultItemFactory = get(parameters = {parametersOf(scope)}),
//        noticeItemFactory = get(parameters = {parametersOf(scope)})) }

//    factory { (scope : Scope) -> TimelineItemFactory( messageItemFactory = get(parameters = {parametersOf(scope)}),
//        encryptedItemFactory = get(parameters = {parametersOf(scope)}),
//        encryptionItemFactory = get(parameters = {parametersOf(scope)}),
//        noticeItemFactory = get(parameters = {parametersOf(scope)}),
//        defaultItemFactory = get(parameters = {parametersOf(scope)}),
//        roomCreateItemFactory = get(parameters = {parametersOf(scope)}),
//        verificationConclusionItemFactory = get(parameters = {parametersOf(scope)}),
//        userPreferencesProvider = get()) }

//    factory { (scope : Scope) -> TimelineEventController(contentUploadStateTrackerBinder = scope.get(),
//        contentDownloadStateTrackerBinder = scope.get(),
//        timelineMediaSizeProvider = scope.get(),
//        timelineItemFactory = get(parameters = {parametersOf(scope)}),
//        dateFormatter = get(),
//        mergedHeaderItemFactory = get(),
//        backgroundHandler = TimelineAsyncHelper.getBackgroundHandler()) }
//
//    factory { MergedHeaderItemFactory(avatarSizeProvider = get(),
//        avatarRenderer = get(),
//        activeSessionHolder =  get(),
//        chatPreferences =  get()) }
//
//    factory { MessageItemAttributesFactory(avatarRenderer = get(),
//        messageColorProvider =  get(),
//        avatarSizeProvider = get(),
//        emojiCompatFontProvider = get()) }

//    factory { MessageActionsController(get(), get(), get()) }
//
//    scope(named<RoomDetailFragment>()) {
//        scoped { TimelineMediaSizeProvider() }
//        scoped { ContentUploadStateTrackerBinder(get(), get(), get()) }
//        scoped { ContentDownloadStateTrackerBinder(get(), get(), get()) }
//        scoped { RoomSummaryHolder() }
//    }
//
//    scope(named<RoomDetailActivity>()) {
//        scoped { MessageSharedActionDataSource() }
//    }
}

val chatModules = arrayListOf(viewModelModules, factoryModule)