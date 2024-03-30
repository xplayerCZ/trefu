package cz.davidkurzica.trefu.data.repository.rest

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.repository.ConnectionRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient

class ConnectionRepositoryImpl(
    private val httpClient: HttpClient,
) : ConnectionRepository {

    override suspend fun getConnectionSets(): Result<List<ConnectionSet>> {
        TODO("Not yet implemented")
    }
}
