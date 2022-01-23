package org.navgurukul.learn.courses.db.models

import com.squareup.moshi.Json
import java.io.Serializable

interface CourseContents: Serializable {
    @Json(name = "id")
    val id: String
    @Json(name = "course_id")
    var courseId: String
    @Json(name = "sequence_num")
    var sequenceNumber: Int
    @Json(name = "content_type")
    var contentContentType: CourseContentType
    var lang: String
    @Json(name = "courseName")
    var courseName: String?
}

enum class CourseContentType(var value: String){
    EXERCISE("exercise"), CLASS("class_topic")
}