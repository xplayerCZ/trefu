package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.util.Result

interface ConnectionRepository {
    suspend fun getConnectionSets(): Result<List<ConnectionSet>>
}