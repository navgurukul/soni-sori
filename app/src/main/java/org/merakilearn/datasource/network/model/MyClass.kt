package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class MyClass(
    @SerializedName("class")
    val classX: Class?,
    @SerializedName("facilitator")
    val facilitator: Facilitator?,
    @SerializedName("feedback")
    val feedback: String?,
    @SerializedName("feedback_at")
    val feedbackAt: String?,
    @SerializedName("registered_at")
    val registeredAt: String?
) {
    data class Class(
        @SerializedName("category_id")
        val categoryId: Int?,
        @SerializedName("course_id")
        val courseId: Any?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("end_time")
        val endTime: String?,
        @SerializedName("exercise_id")
        val exerciseId: Any?,
        @SerializedName("facilitator_id")
        val facilitatorId: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("lang")
        val lang: String?,
        @SerializedName("start_time")
        val startTime: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("video_id")
        val videoId: String?
    )

    data class Facilitator(
        @SerializedName("name")
        val name: String?
    )
}