package cz.davidkurzica.trefu.model

import cz.davidkurzica.util.LocalDateSerializer
import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Connection(
    val connectionsParts: List<ConnectionPart>)

@Serializable
data class ConnectionPart(
    val lineShortCode: String,
    val from: DepartureSimple,
    val to: DepartureSimple
)
