package org.navgurukul.chat.core.resources

import org.navgurukul.chat.features.settings.ChatPreferences

class UserPreferencesProvider(private val chatPreferences: ChatPreferences) {

    fun shouldShowHiddenEvents(): Boolean {
        return chatPreferences.shouldShowHiddenEvents()
    }

    fun shouldShowReadReceipts(): Boolean {
        return chatPreferences.showReadReceipts()
    }

    fun shouldShowRedactedMessages(): Boolean {
        return chatPreferences.showRedactedMessages()
    }

    fun shouldShowLongClickOnRoomHelp(): Boolean {
        return chatPreferences.shouldShowLongClickOnRoomHelp()
    }

    fun neverShowLongClickOnRoomHelpAgain() {
        chatPreferences.neverShowLongClickOnRoomHelpAgain()
    }
}