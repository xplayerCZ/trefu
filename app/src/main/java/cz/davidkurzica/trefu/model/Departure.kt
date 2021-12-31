package cz.davidkurzica.trefu.model

import cz.davidkurzica.util.LocalTimeSerializer
import kotlinx.serialization.Serializable
import org.joda.time.LocalTime

@Serializable
data class Departure(
    val time: @Serializable(with = LocalTimeSerializer::class) LocalTime,
    val lineShortCode: Int,
    val finalStop: String,
)