package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnBoardingData(
    @Json(name = "on_boarding_pages_list")
    val onBoardingPagesList: List<OnBoardingPageData>,
    @Json(name = "on_boarding_pathway_list")
    val onBoardingPathwayList: List<OnBoardingPathwayData>,
    @Json(name = "on_boarding_translations")
    val onBoardingTranslations: Map<String, OnBoardingTranslations>,
)

@JsonClass(generateAdapter = true)
data class OnBoardingTranslations(
    @Json(name = "skip_text")
    val skipText: String,
    @Json(name = "next_text")
    val nextText: String,
    @Json(name = "skip_login_text")
    val skipLoginText: String,
    @Json(name = "login_with_google_text")
    val loginWithGoogleText: String,
    @Json(name = "on_boarding_data_list_texts")
    val onBoardingPageDataListTexts: List<OnBoardingPageDataTranslations>,
    @Json(name = "on_boarding_pathway_list_names")
    val onBoardingPathwayListNames: List<String>,
    @Json(name = "select_course_header")
    val selectCourseHeader: String
)

@JsonClass(generateAdapter = true)
data class OnBoardingPageDataTranslations(
    @Json(name = "header")
    val header: String,
    @Json(name = "description")
    val description: String
)

@JsonClass(generateAdapter = true)
data class OnBoardingImage(
    @Json(name = "local") val local: String? = null,
    @Json(name = "remote") val remote: String? = null
)

@JsonClass(generateAdapter = true)
data class OnBoardingPageData(
    @Json(name = "image")
    val image: OnBoardingImage,
)

@JsonClass(generateAdapter = true)
data class OnBoardingPathwayData(
    @Json(name = "image")
    val image: OnBoardingImage,
    @Json(name = "id")
    val id: Int
)
