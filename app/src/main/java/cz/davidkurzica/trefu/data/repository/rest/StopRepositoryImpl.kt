package cz.davidkurzica.trefu.data.repository.rest

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import cz.davidkurzica.trefu.data.mappers.toStopOption
import cz.davidkurzica.trefu.data.remote.StopOptionsQuery
import cz.davidkurzica.trefu.data.remote.type.StopFilters
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate

class StopRepositoryImpl(
    private val httpClient: HttpClient,
) : StopRepository {
    override suspend fun getStopOptions(forDate: LocalDate): Result<List<StopOption>> {
        return try {
            Result.Success(
                data = httpClient.get("/routing/stops") {
                    parameter("forDate", forDate)
                }.body()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}