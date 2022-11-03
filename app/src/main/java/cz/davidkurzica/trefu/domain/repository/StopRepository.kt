package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate

interface StopRepository {
    suspend fun getStopOptions(forDate: LocalDate): Result<List<StopOption>>
}