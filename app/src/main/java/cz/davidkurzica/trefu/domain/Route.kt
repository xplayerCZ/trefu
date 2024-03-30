package cz.davidkurzica.trefu.domain

import kotlinx.serialization.Serializable

data class Route(
    val line: Line,
    val direction: RouteDirection,
)

@Serializable
data class RouteDirection(
    val routeId: Int,
    val line: Line,
    val direction: String,
) {
    val description = direction
}

@Serializable
data class RouteDAO(
    val routeId: Int,
    val lineShortCode: String,
    val lastStopName: String,
)
