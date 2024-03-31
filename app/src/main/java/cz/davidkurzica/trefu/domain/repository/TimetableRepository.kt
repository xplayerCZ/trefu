package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.Timetable
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate
import java.time.LocalTime

interface TimetableRepository {
    suspend fun getTimetable(
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
        routeId: Int,
        lineShortCode: String,
    ): Result<Timetable?>
}