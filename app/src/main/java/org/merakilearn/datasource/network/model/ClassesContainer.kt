package org.merakilearn.datasource.network.model


import com.google.gson.annotations.SerializedName

data class ClassesContainer(
    @SerializedName("classes")
    val classes: List<Classes>
)