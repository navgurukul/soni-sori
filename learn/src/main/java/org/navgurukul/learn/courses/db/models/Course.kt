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
    @Json(name = "created_at")
    var createdAt: String? = null,
    @Json(name = "days_to_complete")
    var daysToComplete: String? = null,

    @PrimaryKey(autoGenerate = false)
    @Json(name = "id")
    var id: String = "",
    @Json(name = "logo")
    var logo: String? = null,
    @Json(name = "name")
    var name: String = "",
    @Json(name = "notes")
    var notes: String? = null,
    @Json(name = "pathwayId")
    var pathwayId: Int? = null,
    @Json(name = "pathwayName")
    var pathwayName: String? = null,
    @Json(name = "sequence_num")
    var sequenceNum: String? = null,
    @Json(name = "short_description")
    var shortDescription: String? = null,
    @Json(name = "type")
    var type: String? = null,
    @Json(name = "lang_available")
    @ColumnInfo(name = "supportedLanguages", defaultValue = "[\"en\"]")
    var supportedLanguages: List<String> = listOf("en"),
    @Ignore
    @Json(name = "number")
    var number: Int? = null,

    @Ignore
    @Json(name = "exercises")
    var exercises: List<Exercise?>? = listOf()
)

enum class CourseType{
    json, markdown
}