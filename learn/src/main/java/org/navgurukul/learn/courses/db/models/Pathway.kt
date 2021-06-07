package org.navgurukul.learn.courses.db.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "pathway")
@JsonClass(generateAdapter = true)
data class Pathway @JvmOverloads constructor(
    @Json(name = "code")
    val code: String,
    @Ignore
    @Json(name = "courses")
    var courses: List<Course> = arrayListOf(),
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @PrimaryKey(autoGenerate = false)
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "logo")
    val logo: String?,
)