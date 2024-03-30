package cz.davidkurzica.trefu.data.repository.rest

import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.Route
import cz.davidkurzica.trefu.domain.RouteDAO
import cz.davidkurzica.trefu.domain.RouteDirection
import cz.davidkurzica.trefu.domain.repository.RouteRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate

class RouteRepositoryImpl(
    private val httpClient: HttpClient,
) : RouteRepository {
    override suspend fun getRouteOptions(forDate: LocalDate, stopId: Int): Result<List<Route>> {
        return try {
            Result.Success(
                data = httpClient.get("/routing/routes") {
                    parameter("forDate", forDate)
                    parameter("stopId", stopId)
                }.body<List<RouteDAO>>().map {
                    val line = Line(it.lineShortCode)
                    Route(
                        line = line,
                        direction = RouteDirection(
                            routeId = it.routeId,
                            line = line,
                            direction = it.lastStopName
                        )
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun getRoute(id: Int) = TODO()
}