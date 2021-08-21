package org.navgurukul.learn.courses.db.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

interface ExerciseSlugDetail : Serializable {
    val type: String

    companion object {
        const val TYPE_MD = "markdown"
        const val TYPE_PYTHON = "python"
        const val TYPE_JS = "javascript"
        const val TYPE_SOLUTION = "solution"
        const val TYPE_YOUTUBE_VIDEO = "youtube"
        const val TYPE_IMAGE = "image"
        const val TYPE_TRY_TYPING = "trytyping"
        const val TYPE_PRACTICE_TYPING = "practicetyping"
    }
}

@JsonClass(generateAdapter = true)
data class MarkDownExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: String?
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class UnknownExerciseSlugDetail(
    @Json(name = "type")
    override val type: String = "unknown",
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class CodeExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: Code? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class YoutubeExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: String? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class ImageExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: Image? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class TypingExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: List<String>
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class DefaultTypingExerciseSlugDetail(
    @Json(name = "type")
    override val type: String,
    @Json(name = "value")
    var value: Any? = null
) : ExerciseSlugDetail

@JsonClass(generateAdapter = true)
data class Code(
    @Json(name = "code") val code: String?,
    @Json(name = "testCases") val testCases: Any?
)

@JsonClass(generateAdapter = true)
data class Image(@Json(name = "url") val url: String?)