package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class MyClassContainer(
    @SerializedName("classes")
    var myClass: List<MyClass?>?
) {
    data class MyClass(
        @SerializedName("class_id")
        var classId: Int?,
        @SerializedName("classes")
        var classes: List<ClassesContainer.Classes>,
        @SerializedName("feedback")
        var feedback: String?,
        @SerializedName("feedback_at")
        var feedbackAt: String?,
        @SerializedName("id")
        var id: Int?,
        @SerializedName("registered_at")
        var registeredAt: String?,
        @SerializedName("user_id")
        var userId: Int?
    )
}