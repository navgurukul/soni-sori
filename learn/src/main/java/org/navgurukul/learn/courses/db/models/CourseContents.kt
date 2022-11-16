package org.navgurukul.learn.courses.db.models

import androidx.room.ColumnInfo
import com.squareup.moshi.Json

interface CourseContents {
    var courseId: String

    val id: String

//    @Json(name = "name")
//    val name: String

//    var lang: String

    var courseName: String?

    var sequenceNumber: Int?

    var courseContentType: CourseContentType

    var courseContentProgress: CourseContentProgress?

}

enum class CourseContentType{
    class_topic, exercise, assessment
}

enum class CourseContentProgress{
    NOT_STARTED, IN_PROGRESS, COMPLETED, COMPLETED_RESELECT
}


