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

    var sequenceNumber: Int

    var contentContentType: CourseContentType

    var courseContentProgress: CourseContentProgress?

    companion object {
        const val COURSE_CONTENT_EXERCISE = "exercise"
        const val COURSE_CONTENT_CLASS = "class"
    }
}


enum class CourseContentType{
    CLASS_TOPIC, EXERCISE
}

enum class CourseContentProgress{
    NOT_STARTED, IN_PROGRESS, COMPLETED
}


