package org.navgurukul.chat.features.home.room.detail.timeline

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.VisibilityState
import org.matrix.android.sdk.api.session.room.model.message.MessageImageInfoContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.navgurukul.chat.core.date.SaralDateFormatter
import org.navgurukul.chat.core.extensions.localDateTime
import org.navgurukul.chat.core.extensions.nextOrNull
import org.navgurukul.chat.features.home.room.detail.RoomDetailAction
import org.navgurukul.chat.features.home.room.detail.RoomDetailViewState
import org.navgurukul.chat.features.home.room.detail.UnreadState
import org.navgurukul.chat.features.home.room.detail.timeline.factory.MergedHeaderItemFactory
import org.navgurukul.chat.features.home.room.detail.timeline.factory.TimelineItemFactory
import org.navgurukul.chat.features.home.room.detail.timeline.factory.TimelineMediaSizeProvider
import org.navgurukul.chat.features.home.room.detail.timeline.helper.*
import org.navgurukul.chat.features.home.room.detail.timeline.item.*
import org.navgurukul.chat.features.media.ImageContentRenderer
import org.navgurukul.chat.features.media.VideoContentRenderer
import org.threeten.bp.LocalDateTime

class TimelineEventController(
    private val contentUploadStateTrackerBinder: ContentUploadStateTrackerBinder,
    private val contentDownloadStateTrackerBinder: ContentDownloadStateTrackerBinder,
    private val timelineMediaSizeProvider: TimelineMediaSizeProvider,
    private val timelineItemFactory: TimelineItemFactory,
    private val dateFormatter: SaralDateFormatter,
    private val mergedHeaderItemFactory: MergedHeaderItemFactory,
    private val backgroundHandler: Handler
) : EpoxyController(backgroundHandler, backgroundHandler), Timeline.Listener,
    EpoxyController.Interceptor {


    interface Callback : BaseCallback, AvatarCallback, ReactionPillCallback, UrlClickCallback, ReadReceiptsCallback{
        fun onEditedDecorationClicked(informationData: MessageInformationData)
        fun onImageMessageClicked(
            messageImageContent: MessageImageInfoContent,
            mediaData: ImageContentRenderer.Data,
            view: View
        )

        fun onVideoMessageClicked(
            messageVideoContent: MessageVideoContent,
            mediaData: VideoContentRenderer.Data,
            view: View
        )


        // TODO move all callbacks to this?
        fun onTimelineItemAction(itemAction: RoomDetailAction)
    }

    interface ReactionPillCallback {
        fun onClickOnReactionPill(informationData: MessageInformationData, reaction: String, on: Boolean)
        fun onLongClickOnReactionPill(informationData: MessageInformationData, reaction: String)
    }

    interface BaseCallback {
        fun onEventCellClicked(
            informationData: MessageInformationData,
            messageContent: Any?,
            view: View
        )

        fun onEventLongClicked(
            informationData: MessageInformationData,
            messageContent: Any?,
            view: View
        ): Boolean
    }

    interface AvatarCallback {
        fun onAvatarClicked(informationData: MessageInformationData)
        fun onMemberNameClicked(informationData: MessageInformationData)
    }

    interface ReadReceiptsCallback {
        fun onReadMarkerVisible()
    }

    interface UrlClickCallback {
        fun onUrlClicked(url: String, title: String): Boolean
        fun onUrlLongClicked(url: String): Boolean
    }

    private var showingForwardLoader = false

    // Map eventId to adapter position
    private val adapterPositionMapping = HashMap<String, Int>()
    private val modelCache = arrayListOf<CacheItemData?>()
    private var currentSnapshot: List<TimelineEvent> = emptyList()
    private var inSubmitList: Boolean = false
    private var unreadState: UnreadState = UnreadState.Unknown
    private var positionOfReadMarker: Int? = null
    private var eventIdToHighlight: String? = null

    var callback: Callback? = null
    var timeline: Timeline? = null

    private val listUpdateCallback = object : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            synchronized(modelCache) {
                assertUpdateCallbacksAllowed()
                (position until (position + count)).forEach {
                    modelCache[it] = null
                }
                requestModelBuild()
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            synchronized(modelCache) {
                assertUpdateCallbacksAllowed()
                val model = modelCache.removeAt(fromPosition)
                modelCache.add(toPosition, model)
                requestModelBuild()
            }
        }

        override fun onInserted(position: Int, count: Int) {
            synchronized(modelCache) {
                assertUpdateCallbacksAllowed()
                (0 until count).forEach {
                    modelCache.add(position, null)
                }
                requestModelBuild()
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            synchronized(modelCache) {
                assertUpdateCallbacksAllowed()
                (0 until count).forEach {
                    modelCache.removeAt(position)
                }
                requestModelBuild()
            }
        }
    }

    init {
        addInterceptor(this)
        requestModelBuild()
    }

    // Update position when we are building new items
    override fun intercept(models: MutableList<EpoxyModel<*>>) = synchronized(modelCache) {
        positionOfReadMarker = null
        adapterPositionMapping.clear()
        models.forEachIndexed { index, epoxyModel ->
            if (epoxyModel is BaseEventItem) {
                epoxyModel.getEventIds().forEach {
                    adapterPositionMapping[it] = index
                }
            }
        }
        val currentUnreadState = this.unreadState
        if (currentUnreadState is UnreadState.HasUnread) {
            val position = adapterPositionMapping[currentUnreadState.firstUnreadEventId]?.plus(1)
            positionOfReadMarker = position
            if (position != null) {
                val readMarker = TimelineReadMarkerItem_()
                    .also {
                        it.id("read_marker")
                        it.setOnVisibilityStateChanged(
                            ReadMarkerVisibilityStateChangedListener(
                                callback
                            )
                        )
                    }
                models.add(position, readMarker)
            }
        }
    }

    fun update(viewState: RoomDetailViewState) {
        var requestModelBuild = false
        if (eventIdToHighlight != viewState.highlightedEventId) {
            // Clear cache to force a refresh
            synchronized(modelCache) {
                for (i in 0 until modelCache.size) {
                    if (modelCache[i]?.eventId == viewState.highlightedEventId
                        || modelCache[i]?.eventId == eventIdToHighlight
                    ) {
                        modelCache[i] = null
                    }
                }
            }
            eventIdToHighlight = viewState.highlightedEventId
            requestModelBuild = true
        }
        if (this.unreadState != viewState.unreadState) {
            this.unreadState = viewState.unreadState
            requestModelBuild = true
        }
        if (requestModelBuild) {
            requestModelBuild()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        timeline?.addListener(this)
        timelineMediaSizeProvider.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        timelineMediaSizeProvider.recyclerView = null
        contentUploadStateTrackerBinder.clear()
        contentDownloadStateTrackerBinder.clear()
        timeline?.removeListener(this)
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun buildModels() {
        val timestamp = System.currentTimeMillis()
        showingForwardLoader = LoadingItem_()
            .id("forward_loading_item_$timestamp")
            .setVisibilityStateChangedListener(Timeline.Direction.FORWARDS)
            .addWhenLoading(Timeline.Direction.FORWARDS)

        val timelineModels = getModels()
        add(timelineModels)

        // Avoid displaying two loaders if there is no elements between them
        if (!showingForwardLoader || timelineModels.isNotEmpty()) {
            LoadingItem_()
                .id("backward_loading_item_$timestamp")
                .setVisibilityStateChangedListener(Timeline.Direction.BACKWARDS)
                .addWhenLoading(Timeline.Direction.BACKWARDS)
        }
    }

// Timeline.LISTENER ***************************************************************************

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        submitSnapshot(snapshot)
    }

    override fun onTimelineFailure(throwable: Throwable) {
        // no-op, already handled
    }

    override fun onNewTimelineEvents(eventIds: List<String>) {
        // no-op, already handled
    }

    private fun submitSnapshot(newSnapshot: List<TimelineEvent>) {
        backgroundHandler.post {
            inSubmitList = true
            val diffCallback = TimelineEventDiffUtilCallback(currentSnapshot, newSnapshot)
            currentSnapshot = newSnapshot
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(listUpdateCallback)
            requestModelBuild()
            inSubmitList = false
        }
    }

    private fun assertUpdateCallbacksAllowed() {
        require(inSubmitList || Looper.myLooper() == backgroundHandler.looper)
    }

    private fun getModels(): List<EpoxyModel<*>> {
        buildCacheItemsIfNeeded()
        return modelCache
            .map { cacheItemData ->
                val eventModel =
                    if (cacheItemData == null || mergedHeaderItemFactory.isCollapsed(cacheItemData.localId)) {
                        null
                    } else {
                        cacheItemData.eventModel
                    }
                listOf(eventModel,
                    cacheItemData?.mergedHeaderModel,
                    cacheItemData?.formattedDayModel?.takeIf { eventModel != null || cacheItemData.mergedHeaderModel != null }
                )
            }
            .flatten()
            .filterNotNull()
    }

    private fun buildCacheItemsIfNeeded() = synchronized(modelCache) {
        if (modelCache.isEmpty()) {
            return
        }
        (0 until modelCache.size).forEach { position ->
            // Should be build if not cached or if cached but contains additional models
            // We then are sure we always have items up to date.
            if (modelCache[position] == null || modelCache[position]?.shouldTriggerBuild() == true) {
                modelCache[position] = buildCacheItem(position, currentSnapshot)
            }
        }
    }

    private fun buildCacheItem(currentPosition: Int, items: List<TimelineEvent>): CacheItemData {
        val event = items[currentPosition]
        val nextEvent = items.nextOrNull(currentPosition)
        val date = event.root.localDateTime()
        val nextDate = nextEvent?.root?.localDateTime()
        val addDaySeparator = date.toLocalDate() != nextDate?.toLocalDate()
        val eventModel =
            timelineItemFactory.create(event, nextEvent, eventIdToHighlight, callback).also {
                it.id(event.localId)
                it.setOnVisibilityStateChanged(
                    TimelineEventVisibilityStateChangedListener(
                        callback,
                        event
                    )
                )
            }
        val mergedHeaderModel = mergedHeaderItemFactory.create(
            event,
            nextEvent = nextEvent,
            items = items,
            addDaySeparator = addDaySeparator,
            currentPosition = currentPosition,
            eventIdToHighlight = eventIdToHighlight,
            callback = callback
        ) {
            requestModelBuild()
        }
        val daySeparatorItem = buildDaySeparatorItem(addDaySeparator, date)
        return CacheItemData(
            event.localId,
            event.root.eventId,
            eventModel,
            mergedHeaderModel,
            daySeparatorItem
        )
    }

    private fun buildDaySeparatorItem(
        addDaySeparator: Boolean,
        date: LocalDateTime
    ): DaySeparatorItem? {
        return if (addDaySeparator) {
            val formattedDay = dateFormatter.formatMessageDay(date)
            DaySeparatorItem_().formattedDay(formattedDay).id(formattedDay)
        } else {
            null
        }
    }

    /**
     * Return true if added
     */
    private fun LoadingItem_.addWhenLoading(direction: Timeline.Direction): Boolean {
        val shouldAdd = timeline?.hasMoreToLoad(direction) ?: false
        addIf(shouldAdd, this@TimelineEventController)
        return shouldAdd
    }

    /**
     * Return true if added
     */
    private fun LoadingItem_.setVisibilityStateChangedListener(direction: Timeline.Direction): LoadingItem_ {
        return onVisibilityStateChanged { _, _, visibilityState ->
            if (visibilityState == VisibilityState.VISIBLE) {
                callback?.onTimelineItemAction(RoomDetailAction.LoadMoreTimelineEvents(direction))
            }
        }
    }

    fun searchPositionOfEvent(eventId: String?): Int? = synchronized(modelCache) {
        return adapterPositionMapping[eventId]
    }

    fun getPositionOfReadMarker(): Int? = synchronized(modelCache) {
        return positionOfReadMarker
    }

    private data class CacheItemData(
        val localId: Long,
        val eventId: String?,
        val eventModel: EpoxyModel<*>? = null,
        val mergedHeaderModel: BasedMergedItem<*>? = null,
        val formattedDayModel: DaySeparatorItem? = null
    ) {
        fun shouldTriggerBuild(): Boolean {
            return mergedHeaderModel != null || formattedDayModel != null
        }
    }
}
