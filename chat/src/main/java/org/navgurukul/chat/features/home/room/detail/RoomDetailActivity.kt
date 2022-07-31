package org.navgurukul.chat.features.home.room.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.replaceFragment
import org.navgurukul.commonui.platform.ToolbarConfigurable

class RoomDetailActivity : ChatBaseActivity(), ToolbarConfigurable, KoinScopeComponent {

    override val scope: Scope by lazy { activityScope() }

    private lateinit var currentRoomId: String

    private val requireActiveMembershipViewModel: RequireActiveMembershipViewModel by viewModel(parameters = {
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

        requireActiveMembershipViewModel.viewEvents.observe(this, Observer {
            when (it) {
                is RequireActiveMembershipViewEvents.RoomLeft -> handleRoomLeft(it)
            }
        })

    }

    private fun handleRoomLeft(roomLeft: RequireActiveMembershipViewEvents.RoomLeft) {
        if (roomLeft.leftMessage != null) {
            Toast.makeText(this, roomLeft.leftMessage, Toast.LENGTH_LONG).show()
        }
        finish()
    }

    override fun configure(toolbar: Toolbar) {
        configureToolbar(toolbar)
    }

    override fun configure(
        title: String,
        @AttrRes colorRes: Int,
        showProfile: Boolean,
        subtitle: String?,
        onClickListener: View.OnClickListener?,
        action: String?,
        actionOnClickListener: View.OnClickListener?,
        showLogout: Boolean,
        showPathwayIcon : Boolean,
        pathwayIcon: String?
    ) {
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