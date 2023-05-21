package org.navgurukul.chat.features.roomprofile

import com.airbnb.epoxy.TypedEpoxyController
import org.navgurukul.chat.R
import org.navgurukul.chat.core.epoxy.expandableTextItem
import org.navgurukul.chat.core.epoxy.profiles.buildProfileAction
import org.navgurukul.chat.core.epoxy.profiles.buildProfileSection
import org.navgurukul.chat.core.resources.ColorProvider
import org.navgurukul.commonui.resources.StringProvider

class RoomProfileController(
    private val stringProvider: StringProvider,
    colorProvider: ColorProvider
) : TypedEpoxyController<RoomProfileViewState>() {

    private val dividerColor = colorProvider.getColorFromAttribute(R.attr.borderColor)

    var callback: Callback? = null

    interface Callback {
        fun onMemberListClicked()
        fun onBannedMemberListClicked()
        fun onUploadsClicked()
        fun onSettingsClicked()
        fun onLeaveRoomClicked()
        fun onNotificationsClicked()
    }

    override fun buildModels(data: RoomProfileViewState?) {
        if (data == null) {
            return
        }
        val roomSummary = data.roomSummary() ?: return

        // Topic
        roomSummary
            .topic
            .takeIf { it.isNotEmpty() }
            ?.let {
                buildProfileSection(stringProvider.getString(R.string.room_settings_topic))
                expandableTextItem {
                    id("topic")
                    content(it)
                    maxLines(2)
                }
            }


        // More
        buildProfileSection(stringProvider.getString(R.string.room_profile_section_more))
        buildProfileAction(
            id = "notifications",
            title = stringProvider.getString(R.string.room_profile_section_more_notifications),
            dividerColor = dividerColor,
            icon = R.drawable.ic_room_profile_notification,
            action = { callback?.onNotificationsClicked() }
        )
        val numberOfMembers = roomSummary.joinedMembersCount ?: 0
        buildProfileAction(
            id = "member_list",
            title = stringProvider.getQuantityString(R.plurals.room_profile_section_more_member_list, numberOfMembers, numberOfMembers),
            dividerColor = dividerColor,
            icon = R.drawable.ic_room_profile_member_list,
            accessory = 0,
            action = { callback?.onMemberListClicked() }
        )

        if (data.bannedMembership.invoke()?.isNotEmpty() == true) {
            buildProfileAction(
                id = "banned_list",
                title = stringProvider.getString(R.string.room_settings_banned_users_title),
                dividerColor = dividerColor,
                icon = R.drawable.ic_settings_root_labs,
                action = { callback?.onBannedMemberListClicked() }
            )
        }
        buildProfileAction(
            id = "leave",
            title = stringProvider.getString(if (roomSummary.isDirect) {
                R.string.direct_room_profile_section_more_leave
            } else {
                R.string.room_profile_section_more_leave
            }),
            dividerColor = dividerColor,
            divider = false,
            destructive = true,
            icon = R.drawable.ic_room_actions_leave,
            editable = false,
            action = { callback?.onLeaveRoomClicked() }
        )
    }
}
