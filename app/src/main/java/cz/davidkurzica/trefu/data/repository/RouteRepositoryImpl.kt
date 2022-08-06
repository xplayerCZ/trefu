package cz.davidkurzica.trefu.data.repository

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.data.mappers.toRoute
import cz.davidkurzica.trefu.data.remote.RouteOptionsQuery
import cz.davidkurzica.trefu.domain.Route
import cz.davidkurzica.trefu.domain.repository.RouteRepository
import cz.davidkurzica.trefu.domain.util.Result

class RouteRepositoryImpl(
    private val apolloClient: ApolloClient,
) : RouteRepository {
    override suspend fun getRouteOptions(stopId: Int): Result<List<Route>> {
        return try {
            Result.Success(
                data = apolloClient
                    .query(RouteOptionsQuery(stopId.toString()))
                    .execute()
                    .dataAssertNoErrors
                    .stop
                    .routeStops
                    .map { it.route.toRoute() }
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