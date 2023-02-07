package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDetail(
    val center: Any? = null,
    val chat_id: String? = null,
    val chat_password: String? = null,
    val contact: Any? = null,
    val created_at: String? = null,
    val email: String? = null,
    val github_link: Any? = null,
    val google_user_id: String? = null,
    val id: String? = null,
    val lang_1: Any? = null,
    val lang_2: Any? = null,
    val linkedin_link: Any? = null,
    val medium_link: Any? = null,
    val mode: String? = null,
    val name: String? = null,
    val partner_id: Int? = null,
    val pathway_id: Any? = null,
    val pathwaysV2: List<Any>? = null,
    val profile_picture: String? = null,
    val rolesList: List<String>? = null
)