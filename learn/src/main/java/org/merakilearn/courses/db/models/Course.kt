package org.merakilearn.courses.db.models


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "pathway_course")
data class Course(
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("days_to_complete")
    var daysToComplete: String? = null,

    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: String = "",
    @SerializedName("logo")
    var logo: String? = null,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("notes")
    var notes: String? = null,
    @SerializedName("pathwayId")
    var pathwayId: Int? = null,
    @SerializedName("pathwayName")
    var pathwayName: String? = null,
    @SerializedName("sequence_num")
    var sequenceNum: String? = null,
    @SerializedName("short_description")
    var shortDescription: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @Ignore
    var number: Int? = null,

    @Ignore
    @SerializedName("exercises")
    var exercises: List<Exercise?>? = listOf()
)