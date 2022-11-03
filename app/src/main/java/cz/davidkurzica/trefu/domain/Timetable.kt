package cz.davidkurzica.trefu.domain

import cz.davidkurzica.trefu.util.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Timetable(
    val date: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val lineShortCode: String,
    val departures: List<DepartureSimple>,
)

data class TimetableFormData(
    val selectedStop: StopOption,
    val selectedLine: Line,
    val selectedDirection: RouteDirection,
)
