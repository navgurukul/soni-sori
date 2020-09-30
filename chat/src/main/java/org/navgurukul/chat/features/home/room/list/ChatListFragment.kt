package org.navgurukul.chat.features.home.room.list

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import org.koin.android.ext.android.inject
import org.navgurukul.chat.R
import org.navgurukul.chat.features.navigator.ChatNavigator
import org.navgurukul.commonui.platform.BaseFragment

class ChatListFragment : BaseFragment() {

    //private val viewModel: ChaListViewModel by viewModel()
    private val navigator: ChatNavigator by inject()
    // private val adapter: ChatListAdapter by lazy { ChatListAdapter(requireContext()) }

    override fun getLayoutResId(): Int = R.layout.fragment_chat

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*  recycler_view.layoutManager = LinearLayoutManager(context)
          recycler_view.adapter = adapter
          viewModel.rooms.observe(viewLifecycleOwner, Observer {
              adapter.update(it)
          })

          viewModel.viewEvents.observe(viewLifecycleOwner, Observer  {
              when (it) {
                  is RoomListViewEvents.Loading    -> showLoading(it.message)
                  is RoomListViewEvents.Failure    -> showFailure(it.throwable)
                  is RoomListViewEvents.SelectRoom -> handleSelectRoom(it)
                  is RoomListViewEvents.Done -> Unit
              }
          })*/
        setHeaderTitle()
    }

    private fun handleSelectRoom(event: RoomListViewEvents.SelectRoom) {
        navigator.openRoom(requireActivity(), event.roomSummary.roomId)
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
        private const val TAG = "ChatListFragment"
    }
}