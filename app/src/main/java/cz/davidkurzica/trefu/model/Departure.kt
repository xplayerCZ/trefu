package cz.davidkurzica.trefu.model

import org.joda.time.LocalTime

data class Departure(
    val time: LocalTime,
    val lineNumber: Int,
    val finalStop: String,
)