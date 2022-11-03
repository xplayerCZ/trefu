package cz.davidkurzica.trefu.data.mappers

import cz.davidkurzica.trefu.data.remote.DeparturesResultQuery
import cz.davidkurzica.trefu.data.remote.TimetablesResultQuery
import cz.davidkurzica.trefu.domain.DepartureSimple
import cz.davidkurzica.trefu.domain.DepartureWithLine

fun DeparturesResultQuery.Departure.toDepartureItem(): DepartureWithLine {
    return DepartureWithLine(
        time = this.time,
        lineShortCode = this.connection.route.line.shortCode,
        stopName = this.connection.route.lastRouteStop.first().stop.name,
    )
}

fun TimetablesResultQuery.Departure.toDepartureItem(): DepartureSimple {
    return DepartureSimple(
        time = this.time,
        stopName = this.connection.route.lastRouteStop.first().stop.name,
    )
}
