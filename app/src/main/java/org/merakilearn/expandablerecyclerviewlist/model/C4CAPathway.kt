package org.merakilearn.expandablerecyclerviewlist.model

data class C4CAPathway(
    val code: String,
    val createdAt: String,
    val description: String,
    val id: Int,
    val locale: String,
    val logo: String,
    val modules: List<Module>,
    val name: String,
    val outcomes: List<Outcome>,
    val publishedAt: String,
    val summary: List<Summary>,
    val type: String,
    val updatedAt: String,
    val video_link: Any
)
data class Course(
    val createdAt: String,
    val id: Int,
    val lang_available: List<String>,
    val locale: String,
    val logo: String,
    val name: String,
    val publishedAt: String,
    val short_description: String,
    val updatedAt: String
)
data class Module(
    val courses: List<Course>,
    val createdAt: String,
    val id: Int,
    val name: String,
    val publishedAt: String,
    val updatedAt: String
)
data class Outcome(
    val component: String,
    val value: String
)
data class Summary(
    val component: String,
    val value: String
)