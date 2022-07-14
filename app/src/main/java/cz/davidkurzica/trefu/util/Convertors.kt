package cz.davidkurzica.trefu.util

import cz.davidkurzica.trefu.LineOptionsQuery
import cz.davidkurzica.trefu.StopOptionsQuery
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop

fun StopOptionsQuery.Stop.toStop() =
    Stop(
        id = this.id.toInt(),
        name = this.name,
        enabled = true,
    )

fun LineOptionsQuery.Line.toLine() =
    Line(
        id = this.id.toInt(),
        shortCode = this.shortCode,
    )