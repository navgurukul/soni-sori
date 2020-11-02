package org.navgurukul.chat.features.home.room.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.replaceFragment
import org.navgurukul.commonui.platform.ToolbarConfigurable

class RoomDetailActivity : ChatBaseActivity(), ToolbarConfigurable {

    private lateinit var currentRoomId: String

    private val viewModel: RoomDetailViewModel by viewModel(parameters = {
        parametersOf(
            currentRoomId
        )
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        val roomDetailArgs: RoomDetailArgs =
            intent?.extras?.getParcelable(EXTRA_ROOM_DETAIL_ARGS) ?: return

        currentRoomId = roomDetailArgs.roomId

        if (savedInstanceState == null) {

            replaceFragment(
                R.id.roomDetailContainer,
                RoomDetailFragment::class.java,
                roomDetailArgs
            )
        }

        viewModel.viewEvents.observe(this, Observer {
            when (it) {
                is RoomDetailViewEvents.RoomLeft -> handleRoomLeft(it)
            }
        })

    }

    private fun handleRoomLeft(roomLeft: RoomDetailViewEvents.RoomLeft) {
        if (roomLeft.leftMessage != null) {
            Toast.makeText(this, roomLeft.leftMessage, Toast.LENGTH_LONG).show()
        }
        finish()
    }

    override fun configure(toolbar: Toolbar) {
        configureToolbar(toolbar)
    }

    override fun setTitle(title: String, @AttrRes colorRes: Int) {
        throw RuntimeException("Set Title is not supported")
    }

    companion object {

        const val EXTRA_ROOM_DETAIL_ARGS = "EXTRA_ROOM_DETAIL_ARGS"

        fun newIntent(context: Context, roomDetailArgs: RoomDetailArgs): Intent {
            return Intent(context, RoomDetailActivity::class.java).apply {
                putExtra(EXTRA_ROOM_DETAIL_ARGS, roomDetailArgs)
            }
        }
    }
}