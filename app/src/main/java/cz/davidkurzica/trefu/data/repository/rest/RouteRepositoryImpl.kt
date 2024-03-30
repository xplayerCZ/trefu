package cz.davidkurzica.trefu.data.repository.rest

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.data.mappers.toRoute
import cz.davidkurzica.trefu.data.remote.RouteOptionsQuery
import cz.davidkurzica.trefu.domain.Route
import cz.davidkurzica.trefu.domain.repository.RouteRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class RouteRepositoryImpl(
    private val httpClient: HttpClient,
) : RouteRepository {
    override suspend fun getRouteOptions(stopId: Int): Result<List<Route>> {
        return try {
            Result.Success(
                data = httpClient.get("/routing/routes") {
                    parameter("stopId", stopId)
                }.body()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun getRoute(id: Int): Result<Route> {
        TODO("Not yet implemented")
    }

}