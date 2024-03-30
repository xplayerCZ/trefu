package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import cz.davidkurzica.trefu.data.mappers.toStopOption
import cz.davidkurzica.trefu.data.remote.StopOptionsQuery
import cz.davidkurzica.trefu.data.remote.type.StopFilters
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate

class StopRepositoryImpl(
    private val apolloClient: ApolloClient,
) : StopRepository {
    override suspend fun getStopOptions(forDate: LocalDate): Result<List<StopOption>> {
        return try {
            Result.Success(
                data = apolloClient
                    .query(
                        StopOptionsQuery(
                            filter = Optional.Present(
                                StopFilters(
                                    forDate = Optional.Present(forDate)
                                )
                            )
                        )
                    )
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