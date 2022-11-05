package org.merakilearn.datasource.network.model

import com.squareup.moshi.Json

data class PartnerDataApi(
    val description: String,
    val logo: String,
    val name: String,
    val website_link: String
)