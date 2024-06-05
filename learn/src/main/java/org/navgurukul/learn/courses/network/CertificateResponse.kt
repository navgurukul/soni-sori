package org.navgurukul.learn.courses.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "completed_portion")
@JsonClass(generateAdapter = true)
data class GetCompletedPortion(
    @PrimaryKey(autoGenerate = true)
    @Json(name = "total_completed_portion")
    val totalCompletedPortion : Int,
    @Json(name = "pathway")
    var pathway: List<PathwayData>
)

@Entity(tableName = "pathway_data")
@JsonClass(generateAdapter = true)
data class PathwayData(
    @PrimaryKey(autoGenerate = true)
    @Json(name = "course_id")
    val courseId: Int,
    @Json(name = "completed_portion")
    val completedPortion: Int
)

@JsonClass(generateAdapter = true)
data class CertificateResponse(
    @Json(name = "url")
    val url : String
)