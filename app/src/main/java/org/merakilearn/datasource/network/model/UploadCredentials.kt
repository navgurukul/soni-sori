package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadCredentials(
    @Json(name = "data")
    var data: Data?
)

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "Bucket")
    val Bucket : String,

    @Json(name = "Key")
    val Key : String,

    @Json(name = "ContentType")
    val ContentType : String,

    @Json(name = "ACL")
    val ACL : String,

    @Json(name = "Body")
    val Body : String?,

    @Json(name = "Credentials")
    val Credentials : Credentials,

    @Json(name = "project_id")
    val project_id : String
)

@JsonClass(generateAdapter = true)
data class Credentials(
    @Json(name = "AccessKeyId")
    val AccessKeyId : String,

    @Json(name = "SecretAccessKey")
    val SecretAccessKey : String,

    @Json(name = "SessionToken")
    val SessionToken : String
)