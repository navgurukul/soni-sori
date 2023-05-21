package org.navgurukul.chat.features.home.room.list

import com.airbnb.epoxy.EpoxyController
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class RoomSummaryController(
    private val roomSummaryItemFactory: RoomSummaryItemFactory
) : EpoxyController() {

    var listener: Listener? = null

    private var viewState: RoomListViewState? = null

    init {
        // We are requesting a model build directly as the first build of epoxy is on the main thread.
        // It avoids to build the whole list of rooms on the main thread.
        requestModelBuild()
    }

    fun update(viewState: RoomListViewState) {
        this.viewState = viewState
        requestModelBuild()
    }

    override fun buildModels() {
        val nonNullViewState = viewState ?: return
        buildRooms(nonNullViewState)
    }

    private fun buildRooms(viewState: RoomListViewState) {
        val roomSummaries = viewState.asyncRooms()
        roomSummaries?.forEach { roomSummary ->
            roomSummaryItemFactory
                .create(
                    roomSummary,
                    listener
                )
                .addTo(this)
        }
    }

    interface Listener  {
        fun onRoomClicked(room: RoomSummary)
        fun onRoomLongClicked(room: RoomSummary): Boolean
        fun onRejectRoomInvitation(room: RoomSummary)
        fun onAcceptRoomInvitation(room: RoomSummary)
    }
}