package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import cz.davidkurzica.trefu.data.mappers.toDepartureItem
import cz.davidkurzica.trefu.data.remote.TimetablesResultQuery
import cz.davidkurzica.trefu.data.remote.type.DepartureFilters
import cz.davidkurzica.trefu.domain.Timetable
import cz.davidkurzica.trefu.domain.repository.TimetableRepository
import cz.davidkurzica.trefu.domain.util.Result
import java.time.LocalDate
import java.time.LocalTime

class TimetableRepositoryImpl(
    private val apolloClient: ApolloClient,
) : TimetableRepository {
    override suspend fun getTimetable(
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
        routeId: Int,
        lineShortCode: String,
    ): Result<Timetable> {
        return try {
            Result.Success(
                data = apolloClient
                    .query(
                        TimetablesResultQuery(
                            filter = Optional.Present(
                                DepartureFilters(
                                    forDate = Optional.Present(forDate),
                                    after = Optional.Present(after),
                                    stopId = Optional.Present(stopId),
                                    routeId = Optional.Present(routeId),
                                )
                            )
                        )
                    )
                    .execute()
                    .dataAssertNoErrors
                    .departures
                    .map { it.toDepartureItem() }
                    .let { Timetable(LocalDate.now(), lineShortCode, it) }

            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}