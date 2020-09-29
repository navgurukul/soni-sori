package org.navgurukul.learn.courses.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_current_study")
data class CurrentStudy(
    @PrimaryKey(autoGenerate = false)
    var courseId: String,
    var courseName: String?,
    var exerciseSlugName: String?,
    var exerciseName: String?,
    var exerciseId: String
) : Serializable
