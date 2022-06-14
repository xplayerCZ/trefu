package cz.davidkurzica.trefu.model

import kotlinx.serialization.Serializable

@Serializable
data class Direction(
    val id: Int,
    val description: String
)