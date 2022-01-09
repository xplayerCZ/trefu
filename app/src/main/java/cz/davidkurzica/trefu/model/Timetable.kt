package cz.davidkurzica.trefu.model

import cz.davidkurzica.util.LocalDateSerializer
import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Timetable(
    val date: @Serializable(with = LocalDateSerializer::class) LocalDate,
    val lineShortCode: String,
    val departures: List<DepartureSimple>
)
