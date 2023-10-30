package org.navgurukul.learn.courses.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PathwayC4CA(
    @Json(name = "code")
    val code: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "locale")
    val locale: String,
    @Json(name = "logo")
    val logo: String,
    @Json(name = "modules")
    val modules: List<Module>? = null,
    @Json(name = "name")
    val name: String,
//    @Json(name = "outcomes")
//    val outcomes: List<Outcome>,
//    @Json(name = "publishedAt")
//    val publishedAt: String,
//    @Json(name = "summary")
//    val summary: List<Summary>,
//    @Json(name = "type")
//    val type: String,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "video_link")
    val video_link: Any?
)
@JsonClass(generateAdapter = true)
data class Outcome(
    @Json(name = "component")
    val component: String,
    @Json(name = "value")
    val value: String
)
@JsonClass(generateAdapter = true)
data class Module(
    @Json(name = "courses")
    val courses: List<Course>,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "publishedAt")
    val publishedAt: String? = null,
    @Json(name = "color")
    val color: String? = null,
    @Json(name = "updatedAt")
    val updatedAt: String
)
@JsonClass(generateAdapter = true)
data class Summary(
    @Json(name = "component")
    val component: String,
    @Json(name = "value")
    val value: String
)
@JsonClass(generateAdapter = true)
data class Course(
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "lang_available")
    val lang_available: List<String>,
    @Json(name = "locale")
    val locale: String,
    @Json(name = "logo")
    val logo: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "publishedAt")
    val publishedAt: String? = null,
    @Json(name = "short_description")
    val short_description: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)