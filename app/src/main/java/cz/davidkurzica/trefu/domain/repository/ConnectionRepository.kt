package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalTime

interface ConnectionRepository {
    suspend fun getConnectionSets(
        fromStopId: Int,
        toStopId: Int,
        after: LocalTime,
    ): Result<List<ConnectionSet>>
}