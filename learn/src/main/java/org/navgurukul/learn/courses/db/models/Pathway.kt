package org.navgurukul.learn.courses.db.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pathway")
data class Pathway @JvmOverloads constructor(
    @SerializedName("code")
    val code: String,
    @Ignore
    @SerializedName("courses")
    var courses: List<Course> = arrayListOf(),
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String?,
)