package org.navgurukul.learn.courses.db.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.merakilearn.core.datasource.model.Language

private val DEFAULT_SUPPORTED_LANGUAGES = listOf(Language("en", "English"))

@Entity(tableName = "pathway")
@JsonClass(generateAdapter = true)
data class Pathway @JvmOverloads constructor(
    @Json(name = "code")
    val code: String,
    @Ignore
    @Json(name = "courses")
    var courses: List<Course> = arrayListOf(),
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "description")
    val description: String,
    @PrimaryKey(autoGenerate = false)
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "logo")
    val logo: String?,
    @Json(name = "shouldShowCertificate")
    val shouldShowCertificate : Boolean,
    @Json(name = "lang_available")
    @ColumnInfo(
        name = "supportedLanguages",
        defaultValue = "[{\"code\": \"en\", \"label\": \"English\"}]"
    )
    var languages: List<Language> = DEFAULT_SUPPORTED_LANGUAGES,
    val cta: PathwayCTA?,
) {
    val supportedLanguages: List<Language>
        get() = if (languages.isNotEmpty()) languages else DEFAULT_SUPPORTED_LANGUAGES
}

@JsonClass(generateAdapter = true)
data class PathwayCTA(
    val value: String,
    val url: String
)