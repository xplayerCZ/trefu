package cz.davidkurzica.trefu.data.repository

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.repository.DepartureRepository
import cz.davidkurzica.trefu.domain.util.Result

class DepartureRepositoryImpl(
    private val apolloClient: ApolloClient,
) : DepartureRepository {

    override suspend fun getDepartures(stationId: Int): Result<List<DepartureWithLine>> {
        TODO("Not yet implemented")
    }
}