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
    var name: String,
    @Json(name = "pathwayId")
    var pathwayId: Int? = null,
    @Json(name = "short_description")
    var shortDescription: String,
    @Json(name = "lang_available")
    @ColumnInfo(name = "supportedLanguages", defaultValue = "[\"en\"]")
    var supportedLanguages: List<String> = listOf("en"),
    @Json(name = "completed_portion")
    var completedPortion : Int? = null
){
    @Ignore
    @Json(name = "exercises")
    var courseContents: List<CourseContents> = listOf()
}