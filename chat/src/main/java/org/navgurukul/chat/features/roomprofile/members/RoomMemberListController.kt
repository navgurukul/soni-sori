package org.navgurukul.chat.features.roomprofile.members

import com.airbnb.epoxy.TypedEpoxyController
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomThirdPartyInviteContent
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.dividerItem
import org.navgurukul.chat.core.epoxy.profiles.buildProfileSection
import org.navgurukul.chat.core.epoxy.profiles.profileMatrixItem
import org.navgurukul.chat.core.extensions.join
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.chat.features.home.AvatarRenderer
import org.navgurukul.commonui.resources.StringProvider

class RoomMemberListController(
    private val avatarRenderer: AvatarRenderer,
    private val stringProvider: StringProvider,
    private val roomMemberSummaryFilter: RoomMemberSummaryFilter,
    colorProvider: ColorProvider
) : TypedEpoxyController<RoomMemberListViewState>() {

    interface Callback {
        fun onRoomMemberClicked(roomMember: RoomMemberSummary)
        fun onThreePidInviteClicked(event: Event)
    }

    private val dividerColor = colorProvider.getColorFromAttribute(R.attr.borderColor)

    var callback: Callback? = null

    init {
        setData(null)
    }

    override fun buildModels(data: RoomMemberListViewState?) {
        data ?: return

        roomMemberSummaryFilter.filter = data.filter

        val roomMembersByPowerLevel = data.roomMemberSummaries.invoke() ?: return
        val threePidInvites = data.threePidInvites()
                ?.filter { event ->
                    event.content.toModel<RoomThirdPartyInviteContent>()
                            ?.takeIf {
                                data.filter.isEmpty() || it.displayName.contains(data.filter, ignoreCase = true)
                            } != null
                }
                .orEmpty()
        var threePidInvitesDone = threePidInvites.isEmpty()

        for ((powerLevelCategory, roomMemberList) in roomMembersByPowerLevel) {
            val filteredRoomMemberList = roomMemberList.filter { roomMemberSummaryFilter.test(it) }
            if (filteredRoomMemberList.isEmpty()) {
                continue
            }

            if (powerLevelCategory == RoomMemberListCategories.USER && !threePidInvitesDone) {
                // If there is no regular invite, display threepid invite before the regular user
                buildProfileSection(
                        stringProvider.getString(RoomMemberListCategories.INVITE.titleRes)
                )

                buildThreePidInvites(data)
                threePidInvitesDone = true
            }

            buildProfileSection(
                    stringProvider.getString(powerLevelCategory.titleRes)
            )
            filteredRoomMemberList.join(
                    each = { _, roomMember ->
                        profileMatrixItem {
                            id(roomMember.userId)
                            matrixItem(roomMember.toMatrixItem())
                            avatarRenderer(avatarRenderer)
                            clickListener { _ ->
                                callback?.onRoomMemberClicked(roomMember)
                            }
                        }
                    },
                    between = { _, roomMemberBefore ->
                        dividerItem {
                            id("divider_${roomMemberBefore.userId}")
                            color(dividerColor)
                        }
                    }
            )
            if (powerLevelCategory == RoomMemberListCategories.INVITE && !threePidInvitesDone) {
                // Display the threepid invite after the regular invite
                dividerItem {
                    id("divider_threepidinvites")
                    color(dividerColor)
                }

                buildThreePidInvites(data)
                threePidInvitesDone = true
            }
        }

        if (!threePidInvitesDone) {
            // If there is not regular invite and no regular user, finally display threepid invite here
            buildProfileSection(
                    stringProvider.getString(RoomMemberListCategories.INVITE.titleRes)
            )

            buildThreePidInvites(data)
        }
    }

    private fun buildThreePidInvites(data: RoomMemberListViewState) {
        data.threePidInvites()
                ?.filter { it.content.toModel<RoomThirdPartyInviteContent>() != null }
                ?.join(
                        each = { idx, event ->
                            event.content.toModel<RoomThirdPartyInviteContent>()
                                    ?.let { content ->
                                        profileMatrixItem {
                                            id("3pid_$idx")
                                            matrixItem(content.toMatrixItem())
                                            avatarRenderer(avatarRenderer)
                                            editable(data.actionsPermissions.canRevokeThreePidInvite)
                                            clickListener { _ ->
                                                callback?.onThreePidInviteClicked(event)
                                            }
                                        }
                                    }
                        },
                        between = { idx, _ ->
                            dividerItem {
                                id("divider3_$idx")
                                color(dividerColor)
                            }
                        }
                )
    }

    private fun RoomThirdPartyInviteContent.toMatrixItem(): MatrixItem {
        return MatrixItem.UserItem("@", displayName = displayName)
    }
}
