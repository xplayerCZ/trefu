package cz.davidkurzica.trefu.data.repository.rest

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import cz.davidkurzica.trefu.data.mappers.toDepartureItem
import cz.davidkurzica.trefu.data.remote.DeparturesResultQuery
import cz.davidkurzica.trefu.data.remote.type.DepartureFilters
import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.repository.DepartureRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate
import java.time.LocalTime

class DepartureRepositoryImpl(
    private val httpClient: HttpClient,
) : DepartureRepository {
    override suspend fun getDepartures(
        limit: Int,
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
    ): Result<List<DepartureWithLine>> {
        return try {
            Result.Success(
                data = httpClient.get("/routing/departures") {
                    parameter("limit", limit)
                    parameter("forDate", forDate)
                    parameter("after", after)
                    parameter("stopId", stopId)
                }.body()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}