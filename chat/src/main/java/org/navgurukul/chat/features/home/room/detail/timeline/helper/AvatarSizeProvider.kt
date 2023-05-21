package org.navgurukul.chat.features.home.room.detail.timeline.helper

import org.navgurukul.chat.core.utils.DimensionConverter

class AvatarSizeProvider(private val dimensionConverter: DimensionConverter) {

    private val avatarStyle = AvatarStyle.MEDIUM

    val avatarSize: Int by lazy {
        dimensionConverter.dpToPx(avatarStyle.avatarSizeDP)
    }

    companion object {

        enum class AvatarStyle(val avatarSizeDP: Int) {
            BIG(50),
            MEDIUM(40),
            SMALL(30),
            NONE(0)
        }
    }
}