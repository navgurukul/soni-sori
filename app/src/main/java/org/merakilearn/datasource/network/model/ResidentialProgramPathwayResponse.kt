package org.merakilearn.datasource.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResidentialProgramPathwayResponse(
    val id: Int
)
