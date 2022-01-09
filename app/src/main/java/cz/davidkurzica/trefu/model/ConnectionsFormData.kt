package cz.davidkurzica.trefu.model

import java.time.LocalTime

data class ConnectionsFormData(
    val selectedStopFrom: Stop,
    val selectedTimeFrom: LocalTime,
    val selectedStopTo: Stop,
    val selectedTimeTo: LocalTime,
)