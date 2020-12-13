package org.navgurukul.chat.features.home.room

import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.navgurukul.chat.R
import org.navgurukul.commonui.resources.StringProvider

class TypingHelper(private val stringProvider: StringProvider) {

    /**
     * Returns a human readable String of currently typing users in the room (excluding yourself).
     */
    fun getTypingMessage(typingUsers: List<SenderInfo>): String {
        return when {
            typingUsers.isEmpty() ->
                ""
            typingUsers.size == 1 ->
                stringProvider.getString(R.string.room_one_user_is_typing, typingUsers[0].disambiguatedDisplayName)
            typingUsers.size == 2 ->
                stringProvider.getString(R.string.room_two_users_are_typing,
                        typingUsers[0].disambiguatedDisplayName,
                        typingUsers[1].disambiguatedDisplayName)
            else                  ->
                stringProvider.getString(R.string.room_many_users_are_typing,
                        typingUsers[0].disambiguatedDisplayName,
                        typingUsers[1].disambiguatedDisplayName)
        }
    }
}