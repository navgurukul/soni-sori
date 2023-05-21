package org.navgurukul.chat.features.reactions.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmojiCategory(
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "emojis") val emojis: List<String>
)
