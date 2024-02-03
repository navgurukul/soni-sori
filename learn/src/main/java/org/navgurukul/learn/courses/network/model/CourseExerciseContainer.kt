package org.navgurukul.learn.courses.network.model


import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.navgurukul.learn.courses.db.models.Course

@JsonClass(generateAdapter = true)
data class CourseExerciseContainer(
    @Json(name = "course")
    val course: Course
)


@JsonClass(generateAdapter = true)
data class CourseExerciseContainerSlug(
    @Json(name = "course")
    val course: Course2
)




@JsonClass(generateAdapter = true)
data class Course2(
    @PrimaryKey(autoGenerate = false)
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    var name: String,
    @Json(name = "pathwayId")
    var pathwayId: Int? = null,
    @Json(name = "short_description")
    var shortDescription: String?,
    @Json(name = "lang_available")
    @ColumnInfo(name = "supportedLanguages", defaultValue = "[\"en\"]")
    var supportedLanguages: List<String> = listOf("en"),
    @Json(name = "completed_portion")
    var completedPortion : Int? = null
){
    @Ignore
    @Json(name = "content")
    var courseContents: List<Content> = listOf()
}



interface Content {
    var courseId: String

    val id: String

    var courseName: String?

    val slugId : String

//    val exercise : List<Exercise>?
//    var courseContentType: CourseContentType

//    var courseContentProgress: CourseContentProgress?

}