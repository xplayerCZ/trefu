package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.util.Result

interface StopRepository {
    suspend fun getStopOptions(): Result<List<StopOption>>
}