package cz.davidkurzica.trefu.data.mappers

import cz.davidkurzica.trefu.data.remote.StopOptionsQuery
import cz.davidkurzica.trefu.domain.StopOption

fun StopOptionsQuery.Stop.toStopOption(): StopOption {
    return StopOption(
        id = this.id.toInt(),
        name = this.name,
        enabled = true,
    )
}


