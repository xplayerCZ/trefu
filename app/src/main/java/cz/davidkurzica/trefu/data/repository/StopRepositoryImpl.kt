package cz.davidkurzica.trefu.data.repository

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.data.mappers.toStopOption
import cz.davidkurzica.trefu.data.remote.StopOptionsQuery
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.util.Result

class StopRepositoryImpl(
    private val apolloClient: ApolloClient,
) : StopRepository {
    override suspend fun getStopOptions(): Result<List<StopOption>> {
        return try {
            Result.Success(
                data = apolloClient
                    .query(StopOptionsQuery())
                    .execute()
                    .dataAssertNoErrors
                    .stops
                    .map { it.toStopOption() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}