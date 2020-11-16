package org.navgurukul.chat.features.reactions.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmojiData(
        @Json(name = "categories") val categories: List<EmojiCategory>,
        @Json(name = "emojis") val emojis: Map<String, EmojiItem>,
        @Json(name = "aliases") val aliases: Map<String, String>
)
