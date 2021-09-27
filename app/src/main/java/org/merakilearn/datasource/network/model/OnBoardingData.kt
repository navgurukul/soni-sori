package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnBoardingData(
    val image: String,
    val resource:String,
    val header: String,
    val description: String
)