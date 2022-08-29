package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate
import java.time.LocalTime

interface DepartureRepository {
    suspend fun getDepartures(
        limit: Int,
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
    ): Result<List<DepartureWithLine>>
}