package cz.davidkurzica.trefu.data.mappers

import cz.davidkurzica.trefu.data.remote.RouteOptionsQuery
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.Route
import cz.davidkurzica.trefu.domain.RouteDirection

fun RouteOptionsQuery.Route.toRoute(): Route {
    return Route(
        line = Line(this.line.shortCode),
        direction = RouteDirection(this.lastRouteStop.first().stop.name)
    )
}
