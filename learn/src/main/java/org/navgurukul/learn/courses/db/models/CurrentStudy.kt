package org.navgurukul.learn.courses.db.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_current_study")
data class CurrentStudy(
    @PrimaryKey(autoGenerate = false)
    var courseId: String,
    var exerciseId: String
) : Parcelable
