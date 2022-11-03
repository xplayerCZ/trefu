package cz.davidkurzica.trefu.domain

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val shortCode: String,
)