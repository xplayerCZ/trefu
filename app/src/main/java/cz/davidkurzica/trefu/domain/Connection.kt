package cz.davidkurzica.trefu.domain

import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class ConnectionSet(
    val connections: List<Connection>,
)

@Serializable
data class Connection(
    val lineShortCode: String,
    val from: DepartureSimple,
    val to: DepartureSimple,
)

data class ConnectionFormData(
    val selectedStopFrom: StopOption,
    val selectedTimeFrom: LocalTime,
    val selectedStopTo: StopOption,
    val selectedTimeTo: LocalTime,
)
