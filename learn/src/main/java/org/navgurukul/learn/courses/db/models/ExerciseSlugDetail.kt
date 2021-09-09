package org.navgurukul.learn.courses.db.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

interface ExerciseSlugDetail : Serializable {
    val component: String

    companion object {
        const val COMPONENT_TEXT = "text"
        const val COMPONENT_LINK = "link"
        const val COMPONENT_CODE = "code"
        const val COMPONENT_BLOCK_QUOTE = "blockquote"
        const val TYPE_PYTHON = "python"
        const val TYPE_JS = "javascript"
        const val TYPE_SOLUTION = "solution"
        const val COMPONENT_YOUTUBE_VIDEO = "youtube"
        const val COMPONENT_IMAGE = "image"
        const val TYPE_TRY_TYPING = "trytyping"
        const val TYPE_PRACTICE_TYPING = "practicetyping"
    }
}

@JsonClass(generateAdapter = true)
data class TextExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: String?
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class BlockQuoteExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: String?
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class LinkExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "text")
    var value: String?,
    @Json(name = "href")
    var link: String?,
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class UnknownExerciseSlugDetail(
    @Json(name = "component")
    override val component: String = "unknown",
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class CodeExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "text") val code: String?,
    @Json(name = "type") val codeTypes: CodeType
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class YoutubeExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: String? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class ImageExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: String? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class TypingExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: List<Char>
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class DefaultTypingExerciseSlugDetail(
    @Json(name = "component")
    override val component: String,
    @Json(name = "value")
    var value: Any? = null
) : ExerciseSlugDetail

enum class CodeType {
    python, javascript
}