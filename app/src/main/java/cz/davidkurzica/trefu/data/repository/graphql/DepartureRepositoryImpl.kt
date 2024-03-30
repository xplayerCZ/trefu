package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import cz.davidkurzica.trefu.data.mappers.toDepartureItem
import cz.davidkurzica.trefu.data.remote.DeparturesResultQuery
import cz.davidkurzica.trefu.data.remote.type.DepartureFilters
import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.repository.DepartureRepository
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate
import java.time.LocalTime

class DepartureRepositoryImpl(
    private val apolloClient: ApolloClient,
) : DepartureRepository {
    override suspend fun getDepartures(
        limit: Int,
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
    ): Result<List<DepartureWithLine>> {
        return try {
            Result.Success(
                data = apolloClient
                    .query(
                        DeparturesResultQuery(
                            filter = Optional.Present(
                                DepartureFilters(
                                    limit = Optional.Present(limit),
                                    forDate = Optional.Present(forDate),
                                    after = Optional.Present(after),
                                    stopId = Optional.Present(stopId),
                                )
                            )
                        )
                    )
                    .execute()
                    .dataAssertNoErrors
                    .departures
                    .map { it.toDepartureItem() }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}