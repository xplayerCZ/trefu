package cz.davidkurzica.trefu.domain

import cz.davidkurzica.trefu.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class DepartureWithLine(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val lineShortCode: String,
    val stopName: String,
)

@Serializable
data class DepartureSimple(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime?,
    val stopName: String,
)

data class DepartureFormData(
    val stopOptions: List<StopOption>,
)