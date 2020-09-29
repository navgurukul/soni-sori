package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class Classes(
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("course_id")
    val courseId: Any?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("end_time")
    val endTime: String?,
    @SerializedName("enrolled")
    val enrolled: Boolean?,
    @SerializedName("exercise_id")
    val exerciseId: Any?,
    @SerializedName("facilitator")
    val facilitator: Facilitator?,
    @SerializedName("facilitator_id")
    val facilitatorId: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("lang")
    val lang: String?,
    @SerializedName("rules")
    val rules: Rules?,
    @SerializedName("start_time")
    val startTime: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("video_id")
    val videoId: String?
) {
    data class Facilitator(
        @SerializedName("name")
        val name: String?
    )

    data class Rules(
        @SerializedName("en")
        val en: String?
    )
}