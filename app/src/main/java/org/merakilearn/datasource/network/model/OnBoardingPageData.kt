package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter=true)
data class OnBoardingPageData(
    val skip_text:String,
    val next_text:String,
    val skip_login_text:String,
    val login_with_google_text:String,
    val onBoardingDataList:List<OnBoardingData>,
    val onBoardingPathwayList:List<PathwayData>,
    val select_course_header:String
)