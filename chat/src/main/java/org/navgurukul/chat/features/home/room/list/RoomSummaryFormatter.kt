package org.navgurukul.chat.features.home.room.list

object RoomSummaryFormatter {

    /**
     * Format the unread messages counter.
     *
     * @param count the count
     * @return the formatted value
     */
    fun formatUnreadMessagesCounter(count: Int): String {
        return if (count > 999) {
            "${count / 1000}.${count % 1000 / 100}k"
        } else {
            count.toString()
        }
    }
}