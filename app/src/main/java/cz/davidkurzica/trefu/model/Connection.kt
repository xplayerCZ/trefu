package cz.davidkurzica.trefu.model

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val connectionsParts: List<ConnectionPart>)

@Serializable
data class ConnectionPart(
    val lineShortCode: String,
    val from: DepartureSimple,
    val to: DepartureSimple
)
