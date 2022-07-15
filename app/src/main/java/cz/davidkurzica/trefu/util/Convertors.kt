package cz.davidkurzica.trefu.util

import cz.davidkurzica.trefu.DirectionOptionsQuery
import cz.davidkurzica.trefu.LineOptionsQuery
import cz.davidkurzica.trefu.StopOptionsQuery
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop

fun StopOptionsQuery.Stop.toStop() =
    Stop(
        id = this.id.toInt(),
        name = this.name,
        enabled = true,
    )

fun StopOptionsQuery.Data.toStops() =
    this.stops.map { orig -> orig.toStop() }

fun LineOptionsQuery.Line.toLine() =
    Line(
        id = this.id.toInt(),
        shortCode = this.shortCode,
    )

fun LineOptionsQuery.Data.toLines() =
    this.stop.routeStops.map { routeStop -> routeStop.route.line.toLine() }

fun DirectionOptionsQuery.Data.toDirections() =
    this.line.routes.mapIndexed { i, route ->
        Direction(
            i,
            route.lastRouteStop.first().stop.name
        )
    }
