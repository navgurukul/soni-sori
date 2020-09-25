package org.navgurukul.chat.features.home.room.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.chat.R
import org.navgurukul.chat.features.navigator.ChatNavigator
import org.navgurukul.commonui.platform.BaseFragment

class ChatListFragment : BaseFragment() {

    private val viewModel: ChaListViewModel by viewModel()
    private val navigator: ChatNavigator by inject()
    private val adapter: ChatListAdapter by lazy { ChatListAdapter(requireContext()) }

    override fun getLayoutResId(): Int = R.layout.fragment_chat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = adapter
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
        })

        viewModel.viewEvents.observe(viewLifecycleOwner, Observer  {
            when (it) {
                is RoomListViewEvents.Loading    -> showLoading(it.message)
                is RoomListViewEvents.Failure    -> showFailure(it.throwable)
                is RoomListViewEvents.SelectRoom -> handleSelectRoom(it)
                is RoomListViewEvents.Done       -> Unit
            }
        })
    }

    private fun handleSelectRoom(event: RoomListViewEvents.SelectRoom) {
        navigator.openRoom(requireActivity(), event.roomSummary.roomId)
    }
}