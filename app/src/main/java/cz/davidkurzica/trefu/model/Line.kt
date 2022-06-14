package cz.davidkurzica.trefu.model

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: Int,
    val shortCode: String
)