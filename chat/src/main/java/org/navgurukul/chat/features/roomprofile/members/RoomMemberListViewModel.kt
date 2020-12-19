package org.navgurukul.chat.features.roomprofile.members

import androidx.lifecycle.viewModelScope
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.rx.mapOptional
import org.matrix.android.sdk.rx.rx
import org.matrix.android.sdk.rx.unwrap
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.matrix.android.sdk.internal.util.awaitCallback
import org.navgurukul.chat.core.repo.ActiveSessionHolder
import org.navgurukul.chat.features.powerlevel.PowerLevelsObservableFactory
import org.navgurukul.commonui.platform.BaseViewModel
import org.navgurukul.commonui.platform.ViewEvents

class RoomMemberListViewModel(
    private val initialState: RoomMemberListViewState,
    private val roomMemberSummaryComparator: RoomMemberSummaryComparator,
    activeSessionHolder: ActiveSessionHolder
) : BaseViewModel<RoomMemberListViewEvents, RoomMemberListViewState>(initialState) {

    private val session = activeSessionHolder.getActiveSession()
    private val room = session.getRoom(initialState.roomId)!!

    init {
        observeRoomMemberSummaries()
        observeThirdPartyInvites()
        observeRoomSummary()
        observePowerLevel()
    }

    private fun observeRoomMemberSummaries() {
        val roomMemberQueryParams = roomMemberQueryParams {
            displayName = QueryStringValue.IsNotEmpty
            memberships = Membership.activeMemberships()
        }

        Observable
            .combineLatest(
                room.rx().liveRoomMembers(roomMemberQueryParams),
                room.rx()
                    .liveStateEvent(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                    .mapOptional { it.content.toModel<PowerLevelsContent>() }
                    .unwrap(),
                { roomMembers, powerLevelsContent ->
                    buildRoomMemberSummaries(powerLevelsContent, roomMembers)
                }
            )
            .execute { async ->
                copy(roomMemberSummaries = async)
            }

    }

    private fun observePowerLevel() {
        PowerLevelsObservableFactory(room).createObservable()
            .subscribe {
                val permissions = ActionPermissions(
                    canInvite = PowerLevelsHelper(it).isUserAbleToInvite(session.myUserId),
                    canRevokeThreePidInvite = PowerLevelsHelper(it).isUserAllowedToSend(
                        userId = session.myUserId,
                        isState = true,
                        eventType = EventType.STATE_ROOM_THIRD_PARTY_INVITE
                    )
                )
                setState {
                    copy(actionsPermissions = permissions)
                }
            }.disposeOnClear()
    }

    private fun observeRoomSummary() {
        room.rx().liveRoomSummary()
            .unwrap()
            .execute { async ->
                copy(roomSummary = async)
            }
    }

    private fun observeThirdPartyInvites() {
        room.rx().liveStateEvents(setOf(EventType.STATE_ROOM_THIRD_PARTY_INVITE))
            .execute { async ->
                copy(threePidInvites = async)
            }
    }

    private fun buildRoomMemberSummaries(
        powerLevelsContent: PowerLevelsContent,
        roomMembers: List<RoomMemberSummary>
    ): RoomMemberSummaries {
        val admins = ArrayList<RoomMemberSummary>()
        val moderators = ArrayList<RoomMemberSummary>()
        val users = ArrayList<RoomMemberSummary>(roomMembers.size)
        val customs = ArrayList<RoomMemberSummary>()
        val invites = ArrayList<RoomMemberSummary>()
        val powerLevelsHelper = PowerLevelsHelper(powerLevelsContent)
        roomMembers
            .forEach { roomMember ->
                val userRole = powerLevelsHelper.getUserRole(roomMember.userId)
                when {
                    roomMember.membership == Membership.INVITE -> invites.add(roomMember)
                    userRole == Role.Admin -> admins.add(roomMember)
                    userRole == Role.Moderator -> moderators.add(roomMember)
                    userRole == Role.Default -> users.add(roomMember)
                    else -> customs.add(roomMember)
                }
            }

        return listOf(
            RoomMemberListCategories.ADMIN to admins.sortedWith(roomMemberSummaryComparator),
            RoomMemberListCategories.MODERATOR to moderators.sortedWith(roomMemberSummaryComparator),
            RoomMemberListCategories.CUSTOM to customs.sortedWith(roomMemberSummaryComparator),
            RoomMemberListCategories.INVITE to invites.sortedWith(roomMemberSummaryComparator),
            RoomMemberListCategories.USER to users.sortedWith(roomMemberSummaryComparator)
        )
    }

    fun handle(action: RoomMemberListAction) {
        when (action) {
            is RoomMemberListAction.RevokeThreePidInvite -> handleRevokeThreePidInvite(action)
            is RoomMemberListAction.FilterMemberList -> handleFilterMemberList(action)
            is RoomMemberListAction.OpenOrCreateDm -> handleOpenOrCreateDm(action)
        }
    }

    private fun handleOpenOrCreateDm(action: RoomMemberListAction.OpenOrCreateDm) {
        val existingDmRoomId = session.getExistingDirectRoomWithUser(action.userId)
        if (existingDmRoomId == null) {
            // First create a direct room
            viewModelScope.launch(Dispatchers.IO) {
                val roomId = awaitCallback<String> {
                    session.createDirectRoom(action.userId, it)
                }
                _viewEvents.postValue(RoomMemberListViewEvents.OpenRoom(roomId))
            }
        } else {
            if (existingDmRoomId != initialState.roomId) {
                _viewEvents.postValue(RoomMemberListViewEvents.OpenRoom(existingDmRoomId))
            }
        }
    }

    private fun handleRevokeThreePidInvite(action: RoomMemberListAction.RevokeThreePidInvite) {
        viewModelScope.launch {
            room.sendStateEvent(
                eventType = EventType.STATE_ROOM_THIRD_PARTY_INVITE,
                stateKey = action.stateKey,
                body = emptyMap(),
                callback = NoOpMatrixCallback()
            )
        }
    }

    private fun handleFilterMemberList(action: RoomMemberListAction.FilterMemberList) {
        setState {
            copy(
                filter = action.searchTerm
            )
        }
    }
}

sealed class RoomMemberListViewEvents: ViewEvents {
    data class OpenRoom(val roomId: String) : RoomMemberListViewEvents()
}
