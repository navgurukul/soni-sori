package org.navgurukul.chat.features.home.room.list

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import im.vector.matrix.android.api.failure.Failure
import im.vector.matrix.android.api.session.room.model.Membership
import im.vector.matrix.android.api.session.room.model.RoomSummary
import kotlinx.android.synthetic.main.fragment_room_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.navigator.MerakiNavigator
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.cleanup
import org.navgurukul.chat.features.navigator.ChatNavigator
import org.navgurukul.commonui.model.Fail
import org.navgurukul.commonui.model.Incomplete
import org.navgurukul.commonui.model.Success
import org.navgurukul.commonui.platform.BaseFragment
import org.navgurukul.commonui.platform.StateView

class RoomListFragment : BaseFragment(), RoomSummaryController.Listener {


    private val viewModel: RoomListViewModel by viewModel(parameters = { parametersOf(RoomListViewState()) })
    private val navigator: MerakiNavigator by inject()
    private val roomController: RoomSummaryController by inject()

    override fun getLayoutResId(): Int = R.layout.fragment_room_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            invalidateState(it)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, Observer  {
            when (it) {
                is RoomListViewEvents.Loading    -> showLoading(it.message)
                is RoomListViewEvents.Failure    -> showFailure(it.throwable)
                is RoomListViewEvents.SelectRoom -> handleSelectRoom(it)
                is RoomListViewEvents.Done       -> Unit
            }
        })
        setHeaderTitle()
    }

    private fun invalidateState(state: RoomListViewState) {
        when (state.asyncRooms) {
            is Incomplete -> renderLoading()
            is Success -> renderSuccess(state)
            is Fail -> renderFailure(state.asyncRooms.error)
        }
        roomController.update(state)

        syncStateView.render(state.syncState)
    }

    private fun renderSuccess(state: RoomListViewState) {
        val allRooms = state.asyncRooms()
        if (allRooms.isNullOrEmpty()) {
            renderEmptyState(allRooms)
        } else {
            stateView.state = StateView.State.Content
        }
    }

    private fun renderEmptyState(allRooms: List<RoomSummary>?) {
        val hasNoRoom = allRooms
            ?.filter {
                it.membership == Membership.JOIN || it.membership == Membership.INVITE
            }
            .isNullOrEmpty()
        val emptyState = StateView.State.Empty(
                    getString(R.string.room_list_rooms_empty_title),
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_chat),
                    getString(R.string.room_list_rooms_empty_body))
        stateView.state = emptyState
    }

    private fun renderLoading() {
        stateView.state = StateView.State.Loading
    }

    private fun renderFailure(error: Throwable) {
        val message = when (error) {
            is Failure.NetworkConnection -> getString(R.string.network_error_please_check_and_retry)
            else                         -> getString(R.string.unknown_error)
        }
        stateView.state = StateView.State.Error(message)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        roomListView.layoutManager = layoutManager
        roomListView.itemAnimator = RoomListAnimator()
        layoutManager.recycleChildrenOnDetach = true
        roomController.listener = this
        roomListView.adapter = roomController.adapter
        stateView.contentView = roomListView
    }

    override fun onDestroyView() {
        roomListView.cleanup()
        roomController.listener = null
        super.onDestroyView()
    }


    private fun handleSelectRoom(event: RoomListViewEvents.SelectRoom) {
        navigator.openRoom(requireActivity(), event.roomSummary.roomId)
    }

    override fun onRoomClicked(room: RoomSummary) {
        viewModel.handle(RoomListAction.SelectRoom(room))
    }

    override fun onRoomLongClicked(room: RoomSummary): Boolean {
        return false
    }

    override fun onRejectRoomInvitation(room: RoomSummary) {
    }

    override fun onAcceptRoomInvitation(room: RoomSummary) {
    }

    private fun setHeaderTitle() {
        try {
            val clazz = Class.forName("org.merakilearn.MainActivity")
            val method = clazz.getMethod("setHeaderTitle", String::class.java, Activity::class.java)
            method.invoke(clazz.newInstance(), getString(R.string.title_chat), activity)
        } catch (ex: Exception) {
            Log.e(TAG, "setHeaderTitle: ", ex)
        }
    }

    companion object {
        private const val TAG = "RoomListFragment"
    }
}