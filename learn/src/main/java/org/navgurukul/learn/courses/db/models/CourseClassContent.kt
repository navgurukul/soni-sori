package org.navgurukul.learn.courses.db.models

import androidx.room.Entity
import com.squareup.moshi.JsonClass

@Entity(tableName = "course_class", primaryKeys = ["id"])
@JsonClass(generateAdapter = true)
data class CourseClassContent (
    override val id: String,
    override var courseId: String,
    override var sequenceNumber: Int,
    override var contentContentType: CourseContentType,
    override var lang: String,
    override var courseName: String?
): CourseContents