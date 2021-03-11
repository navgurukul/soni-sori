package org.navgurukul.learn.courses.network.model

import com.google.gson.annotations.SerializedName
import org.navgurukul.learn.courses.db.models.Pathway

data class PathwayContainer(
    @SerializedName("pathways")
    val pathways: List<Pathway>
)