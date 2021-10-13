package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@Entity(tableName = "course_exercise", primaryKeys = ["id", "lang"])
@JsonClass(generateAdapter = true)
data class Exercise(
    @Json(name = "content")
    val content: List<BaseCourseContent>,
    @Json(name = "course_id")
    var courseId: String = "",
    @Json(name = "id")
    @ColumnInfo(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "slug")
    var slug: String?,
    var lang: String = "en",

    @Json(name = "courseName")
    var courseName: String?
) : Serializable {
    @Ignore
    var number: Int? = 0
}