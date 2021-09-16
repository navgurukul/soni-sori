package org.navgurukul.chat.features.roomprofile

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_room_profile.*
import kotlinx.android.synthetic.main.view_stub_room_profile_header.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.merakilearn.core.extentions.fragmentArgs
import org.navgurukul.chat.R
import org.navgurukul.chat.core.animations.AppBarStateChangeListener
import org.navgurukul.chat.core.animations.MerakiItemAppBarStateChangeListener
import org.navgurukul.chat.core.extensions.*
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.chat.features.home.room.list.actions.RoomListActionsArgs
import org.navgurukul.chat.features.home.room.list.actions.RoomListQuickActionsBottomSheet
import org.navgurukul.chat.features.home.room.list.actions.RoomListQuickActionsSharedAction
import org.navgurukul.chat.features.home.room.list.actions.RoomListQuickActionsSharedActionViewModel
import org.navgurukul.chat.features.media.BigImageViewerActivity
import org.navgurukul.commonui.platform.BaseFragment
import timber.log.Timber

@Parcelize
data class RoomProfileArgs(
        val roomId: String
) : Parcelable

class RoomProfileFragment: BaseFragment(),
        RoomProfileController.Callback {

    private val roomProfileController: RoomProfileController by inject()
    private val avatarRenderer: AvatarRenderer by inject()

    private val roomProfileArgs: RoomProfileArgs by fragmentArgs()
    private val roomProfileSharedActionViewModel: RoomProfileSharedActionViewModel by sharedViewModel()
    private val roomListQuickActionsSharedActionViewModel: RoomListQuickActionsSharedActionViewModel by sharedViewModel()
    private val roomProfileViewModel: RoomProfileViewModel by viewModel(parameters = { parametersOf(RoomProfileViewState(roomProfileArgs.roomId))})

    private var appBarStateChangeListener: AppBarStateChangeListener? = null

    override fun getLayoutResId() = R.layout.fragment_room_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val headerView = profileHeaderView.let {
            it.layoutResource = R.layout.view_stub_room_profile_header
            it.inflate()
        }
        setupToolbar(profileToolbar)
        setupRecyclerView()
        appBarStateChangeListener = MerakiItemAppBarStateChangeListener(
            listOf(profileToolbarAvatarImageView,
                    profileToolbarTitleView)
        )
        profileAppBarLayout.addOnOffsetChangedListener(appBarStateChangeListener)
        roomProfileViewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                is RoomProfileViewEvents.Loading          -> showLoading(it.message)
                is RoomProfileViewEvents.Failure          -> showFailure(it.throwable)
            }
        })
        setupLongClicks()

        roomProfileViewModel.viewState.observe(viewLifecycleOwner, Observer {
            setupState(it)
        })

        roomListQuickActionsSharedActionViewModel.viewEvents.observe(viewLifecycleOwner, Observer {
            handleQuickActions(it)
        })
    }

    private fun setupLongClicks() {
        roomProfileNameView.copyOnLongClick()
        roomProfileAliasView.copyOnLongClick()
    }

    private fun setupRecyclerView() {
        roomProfileController.callback = this
        profileRecyclerView.configureWith(roomProfileController, hasFixedSize = true, disableItemAnimation = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileAppBarLayout.removeOnOffsetChangedListener(appBarStateChangeListener)
        profileRecyclerView.cleanup()
        appBarStateChangeListener = null
    }

    private fun setupState(state: RoomProfileViewState) {
        state.roomSummary()?.also {
            if (it.membership.isLeft()) {
                Timber.w("The room has been left")
                activity?.finish()
            } else {
                roomProfileNameView.text = it.displayName
                profileToolbarTitleView.text = it.displayName
                roomProfileAliasView.setTextOrHide(it.canonicalAlias)
                val matrixItem = it.toMatrixItem()
                avatarRenderer.render(matrixItem, roomProfileAvatarView)
                avatarRenderer.render(matrixItem, profileToolbarAvatarImageView)

                roomProfileAvatarView.setOnClickListener { view ->
                    onAvatarClicked(view, matrixItem)
                }
                profileToolbarAvatarImageView.setOnClickListener { view ->
                    onAvatarClicked(view, matrixItem)
                }
            }
        }
        roomProfileController.setData(state)
    }

    // RoomProfileController.Callback
    override fun onMemberListClicked() {
        roomProfileSharedActionViewModel.handle(RoomProfileSharedAction.OpenRoomMembers)
    }

    override fun onBannedMemberListClicked() {
        roomProfileSharedActionViewModel.handle(RoomProfileSharedAction.OpenBannedRoomMembers)
    }

    override fun onSettingsClicked() {
        roomProfileSharedActionViewModel.handle(RoomProfileSharedAction.OpenRoomSettings)
    }

    override fun onUploadsClicked() {
        roomProfileSharedActionViewModel.handle(RoomProfileSharedAction.OpenRoomUploads)
    }

    override fun onLeaveRoomClicked() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.room_participants_leave_prompt_title)
                .setMessage(R.string.room_participants_leave_prompt_msg)
                .setPositiveButton(R.string.leave) { _, _ ->
                    roomProfileViewModel.handle(RoomProfileAction.LeaveRoom)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun onNotificationsClicked() {
        RoomListQuickActionsBottomSheet
            .newInstance(roomProfileArgs.roomId, RoomListActionsArgs.Mode.NOTIFICATIONS)
            .show(childFragmentManager, "ROOM_PROFILE_NOTIFICATIONS")
    }

    private fun handleQuickActions(action: RoomListQuickActionsSharedAction) = when (action) {
        is RoomListQuickActionsSharedAction.NotificationsAllNoisy     -> {
            roomProfileViewModel.handle(RoomProfileAction.ChangeRoomNotificationState(
                RoomNotificationState.ALL_MESSAGES_NOISY))
        }
        is RoomListQuickActionsSharedAction.NotificationsAll          -> {
            roomProfileViewModel.handle(RoomProfileAction.ChangeRoomNotificationState(RoomNotificationState.ALL_MESSAGES))
        }
        is RoomListQuickActionsSharedAction.NotificationsMentionsOnly -> {
            roomProfileViewModel.handle(RoomProfileAction.ChangeRoomNotificationState(RoomNotificationState.MENTIONS_ONLY))
        }
        is RoomListQuickActionsSharedAction.NotificationsMute         -> {
            roomProfileViewModel.handle(RoomProfileAction.ChangeRoomNotificationState(RoomNotificationState.MUTE))
        }
        else                                                          -> Timber.v("$action not handled")
    }

    private fun onAvatarClicked(view: View, matrixItem: MatrixItem.RoomItem) = withState(roomProfileViewModel) {
        matrixItem.avatarUrl
                ?.takeIf { it.isNotEmpty() }
                ?.let { avatarUrl ->
                    val intent = BigImageViewerActivity.newIntent(requireContext(), matrixItem.getBestName(), avatarUrl)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), view, ViewCompat.getTransitionName(view) ?: "")
                    startActivity(intent, options.toBundle())
                }
    }
}