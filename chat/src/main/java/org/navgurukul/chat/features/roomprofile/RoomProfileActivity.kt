package org.navgurukul.chat.features.roomprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.KEY_ARG
import org.navgurukul.chat.ChatBaseActivity
import org.navgurukul.chat.R
import org.navgurukul.chat.core.extensions.addFragment
import org.navgurukul.chat.core.extensions.addFragmentToBackstack
import org.navgurukul.chat.features.home.room.detail.RequireActiveMembershipViewEvents
import org.navgurukul.chat.features.home.room.detail.RequireActiveMembershipViewModel
import org.navgurukul.chat.features.roomprofile.members.RoomMemberListFragment
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.EmptyViewState
import org.navgurukul.commonui.platform.ToolbarConfigurable
import org.navgurukul.commonui.platform.ViewEvents

class RoomProfileActivity :
    ChatBaseActivity(),
    ToolbarConfigurable {

    companion object {

        fun newIntent(context: Context, roomId: String): Intent {
            val roomProfileArgs = RoomProfileArgs(roomId)
            return Intent(context, RoomProfileActivity::class.java).apply {
                putExtra(KEY_ARG, roomProfileArgs)
            }
        }
    }

    private val sharedActionViewModel: RoomProfileSharedActionViewModel by viewModel()
    private lateinit var roomProfileArgs: RoomProfileArgs

    private val requireActiveMembershipViewModel: RequireActiveMembershipViewModel by viewModel(parameters = {
        parametersOf(
            roomProfileArgs.roomId
        )
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple)

        roomProfileArgs = intent?.extras?.getParcelable(KEY_ARG) ?: run {
            finish()
            return
        }
        if (savedInstanceState == null) {
            addFragment(
                R.id.simpleFragmentContainer,
                RoomProfileFragment::class.java,
                roomProfileArgs
            )
        }
        sharedActionViewModel
            .viewEvents.observe(this, Observer { sharedAction ->
                when (sharedAction) {
                    is RoomProfileSharedAction.OpenRoomMembers -> openRoomMembers()
                    is RoomProfileSharedAction.OpenRoomSettings -> openRoomSettings()
                    is RoomProfileSharedAction.OpenRoomUploads -> openRoomUploads()
                    is RoomProfileSharedAction.OpenBannedRoomMembers -> openBannedRoomMembers()
                }

            })

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

    private fun openRoomUploads() {
//        addFragmentToBackstack(
//            R.id.simpleFragmentContainer,
//            RoomUploadsFragment::class.java,
//            roomProfileArgs
//        )
    }

    private fun openRoomSettings() {
//        addFragmentToBackstack(
//            R.id.simpleFragmentContainer,
//            RoomSettingsFragment::class.java,
//            roomProfileArgs
//        )
    }

    private fun openRoomMembers() {
        addFragmentToBackstack(
            R.id.simpleFragmentContainer,
            RoomMemberListFragment::class.java,
            roomProfileArgs
        )
    }

    private fun openBannedRoomMembers() {
//        addFragmentToBackstack(
//            R.id.simpleFragmentContainer,
//            RoomBannedMemberListFragment::class.java,
//            roomProfileArgs
//        )
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
}

class RoomProfileSharedActionViewModel :
    BaseViewModel<RoomProfileSharedAction, EmptyViewState>(EmptyViewState) {
    fun handle(sharedAction: RoomProfileSharedAction) {
        _viewEvents.setValue(sharedAction)
    }
}

/**
 * Supported navigation actions for [RoomProfileActivity]
 */
sealed class RoomProfileSharedAction : ViewEvents {
    object OpenRoomSettings : RoomProfileSharedAction()
    object OpenRoomUploads : RoomProfileSharedAction()
    object OpenRoomMembers : RoomProfileSharedAction()
    object OpenBannedRoomMembers : RoomProfileSharedAction()
}
