package org.navgurukul.learn.courses.db.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "pathway_course")
@JsonClass(generateAdapter = true)
data class Course(
    @PrimaryKey(autoGenerate = false)
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String,
    @Json(name = "pathwayId")
    var pathwayId: Int? = null,
    @Json(name = "short_description")
    val shortDescription: String,
    @Json(name = "lang_available")
    @ColumnInfo(name = "supportedLanguages", defaultValue = "[\"en\"]")
    val supportedLanguages: List<String> = listOf("en"),
){
    @Ignore
    @Json(name = "number")
    var number: Int? = null

    @Ignore
    @Json(name = "exercises")
    val exercises: List<Exercise> = listOf()
}

enum class CourseType{
    json, markdown
}