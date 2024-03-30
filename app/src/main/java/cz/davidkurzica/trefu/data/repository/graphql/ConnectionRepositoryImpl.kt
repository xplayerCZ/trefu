package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.repository.ConnectionRepository
import cz.davidkurzica.trefu.domain.util.Result

class ConnectionRepositoryImpl(
    private val apolloClient: ApolloClient,
) : ConnectionRepository {

    override suspend fun getConnectionSets(): Result<List<ConnectionSet>> {
        TODO("Not yet implemented")
    }
}
