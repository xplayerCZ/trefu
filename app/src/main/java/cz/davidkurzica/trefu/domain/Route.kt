package cz.davidkurzica.trefu.domain

import kotlinx.serialization.Serializable

data class Route(
    val line: Line,
    val direction: RouteDirection,
)

@Serializable
data class RouteDirection(
    val description: String,
)