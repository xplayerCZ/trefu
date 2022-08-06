package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.util.Result

interface DepartureRepository {
    suspend fun getDepartures(stationId: Int): Result<List<DepartureWithLine>>
}