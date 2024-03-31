package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.repository.ConnectionRepository
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalTime

class ConnectionRepositoryImpl(
    private val apolloClient: ApolloClient,
) : ConnectionRepository {

    override suspend fun getConnectionSets(
        fromStopId: Int,
        toStopId: Int,
        after: LocalTime,
    ): Result<List<ConnectionSet>> {
        TODO("Not yet implemented")
    }
}
