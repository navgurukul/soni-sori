package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class MyClass(
    @SerializedName("class")
    val classes: Classes,
    @SerializedName("facilitator")
    val facilitator: Classes.Facilitator?,
    @SerializedName("feedback")
    val feedback: String?,
    @SerializedName("feedback_at")
    val feedbackAt: String?,
    @SerializedName("registered_at")
    val registeredAt: String?
)