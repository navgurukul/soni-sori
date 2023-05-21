package org.navgurukul.chat.features.attachmentviewer

sealed class SwipeDirection {
    object NotDetected : SwipeDirection()
    object Up : SwipeDirection()
    object Down : SwipeDirection()
    object Left : SwipeDirection()
    object Right : SwipeDirection()

    companion object {
        fun fromAngle(angle: Double): SwipeDirection {
            return when (angle) {
                in 0.0..45.0    -> Right
                in 45.0..135.0  -> Up
                in 135.0..225.0 -> Left
                in 225.0..315.0 -> Down
                in 315.0..360.0 -> Right
                else            -> NotDetected
            }
        }
    }
}
