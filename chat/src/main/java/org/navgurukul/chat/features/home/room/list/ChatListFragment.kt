package org.navgurukul.chat.features.home.room.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.navgurukul.chat.R


class ChatListFragment : androidx.fragment.app.Fragment() {

    private val viewModel: ChaListtViewModel by viewModel()
    private val adapter: ChatListAdapter by lazy { ChatListAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = adapter
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            adapter.update(it)
        })


    }
}