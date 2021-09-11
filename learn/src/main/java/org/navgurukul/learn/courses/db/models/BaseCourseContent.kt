package org.navgurukul.learn.courses.db.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

interface BaseCourseContent : Serializable {
    val component: String
    val decoration: Decoration?

    companion object {
        const val COMPONENT_TEXT = "text"
        const val COMPONENT_LINK = "link"
        const val COMPONENT_CODE = "code"
        const val COMPONENT_BLOCK_QUOTE = "blockquote"
        const val COMPONENT_HEADER = "blockquote"
        const val TYPE_PYTHON = "python"
        const val TYPE_JS = "javascript"
        const val TYPE_SOLUTION = "solution"
        const val COMPONENT_YOUTUBE_VIDEO = "youtube"
        const val COMPONENT_IMAGE = "image"
        const val COMPONENT_BANNER = "banner"
    }
}

@JsonClass(generateAdapter = true)
data class TextBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class HeaderBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class BlockQuoteBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class LinkBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "text")
        val value: String?,
        @Json(name = "href")
        val link: String?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class UnknownBaseCourseContent(
        @Json(name = "component")
        override val component: String = "unknown",
        @Json(name = "text")
        val value: String? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class CodeBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "text")
        val value: String?,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "type")
        val codeTypes: CodeType,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class YoutubeBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: String? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class ImageBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: String? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class BannerCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: String?,
        @Json(name = "title")
        var title: String?,
        @Json(name = "action")
        var action: BannerAction?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class Decoration(
        @Json(name = "type")
        val type: DecorationType,
        @Json(name = "value")
        var value: Int?
)

@JsonClass(generateAdapter = true)
data class BannerAction(
        @Json(name = "url")
        val url: String?,
        @Json(name = "label")
        var label: String?
)

enum class CodeType {
    python, javascript
}

enum class DecorationType {
    number, bullet
}