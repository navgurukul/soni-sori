package org.navgurukul.learn.courses.db.models

import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.navgurukul.learn.ui.common.MerakiButtonType
import java.io.Serializable

interface BaseCourseContent : Serializable {
    val component: String
    val decoration: Decoration?

    companion object {
        const val COMPONENT_TEXT = "text"
        const val COMPONENT_LINK = "link"
        const val COMPONENT_CODE = "code"
        const val COMPONENT_BLOCK_QUOTE = "blockquote"
        const val COMPONENT_HEADER = "header"
        const val COMPONENT_TABLE = "table"
        const val TYPE_PYTHON = "python"
        const val TYPE_JS = "javascript"
        const val TYPE_SOLUTION = "solution"
        const val COMPONENT_YOUTUBE_VIDEO = "youtube"
        const val COMPONENT_UNKNOWN = "unknown"
        const val COMPONENT_IMAGE = "image"
        const val COMPONENT_BANNER = "banner"
        const val COMPONENT_SOLUTION = "solution"
        const val COMPONENT_OPTIONS = "options"
        const val COMPONENT_OUTPUT = "output"
        const val COMPONENT_QUESTION_CODE = "questionCode"
        const val COMPONENT_QUESTION_EXPRESSION = "questionExpression"
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
        @Json(name = "variant")
        val variant: Int?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class TableBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: List<TableColumn>?,
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
        @Json(name = "value")
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
        @Json(name = "value")
        val value: String? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class CodeBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "type")
        val codeTypes: CodeType? = null,
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
        @Json(name = "alt")
        var alt: String? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class BannerBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: String,
        @Json(name = "title")
        var title: String?,
        @Json(name = "actions")
        var actions: List<BannerAction>?,
        @Json(name = "image")
        var image: String?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class QuestionCodeBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "type")
        val codeTypes: CodeType? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class QuestionExpressionBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        val value: String?,
        @Json(name = "title")
        val title: String? = null,
        @Json(name = "type")
        val codeTypes: CodeType? = null,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
) : BaseCourseContent

@JsonClass(generateAdapter = true)
data class OptionsBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: List<OptionResponse>,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
): BaseCourseContent

@JsonClass(generateAdapter = true)
data class SolutionBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: Int?,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
):BaseCourseContent

@JsonClass(generateAdapter = true)
data class OutputBaseCourseContent(
        @Json(name = "component")
        override val component: String,
        @Json(name = "value")
        var value: AnswerOutput,
        @Json(name = "decoration")
        override val decoration: Decoration? = null
):BaseCourseContent

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
        var label: String? = null,
        @Json(name = "data")
        var data: String?,
        @Json(name = "icon")
        var icon: String?,
        @Json(name = "variant")
        var variant: MerakiButtonType,
)

@JsonClass(generateAdapter = true)
data class TableColumn(
        @Json(name = "header")
        val header: String?,
        @Json(name = "items")
        var items: List<String>?
)

enum class CodeType {
    python, javascript, other
}

enum class DecorationType {
    number, bullet
}

@JsonClass(generateAdapter = true)
data class OptionResponse(
        @Json(name = "id")
        val id : Int,
        @Json(name = "value")
        val value: String,
        @Ignore
        var viewState: OptionViewState = OptionViewState.NOT_SELECTED
)

@JsonClass(generateAdapter = true)
data class AnswerOutput(
        @Json(name = "correct")
        val correct: List<BaseCourseContent>,
        @Json(name = "incorrect")
        val incorrect: List<BaseCourseContent>,

)

enum class OptionViewState{
        NOT_SELECTED, SELECTED, CORRECT, INCORRECT
}
