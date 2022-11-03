package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.Route
import cz.davidkurzica.trefu.domain.util.Result

interface RouteRepository {
    suspend fun getRouteOptions(stopId: Int): Result<List<Route>>
    suspend fun getRoute(id: Int): Result<Route>
}