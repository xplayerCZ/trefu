package cz.davidkurzica.trefu.data.repository.rest

import cz.davidkurzica.trefu.domain.Timetable
import cz.davidkurzica.trefu.domain.TimetableDAO
import cz.davidkurzica.trefu.domain.repository.TimetableRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.LocalDate
import java.time.LocalTime

class TimetableRepositoryImpl(
    private val httpClient: HttpClient,
) : TimetableRepository {
    override suspend fun getTimetable(
        forDate: LocalDate,
        after: LocalTime,
        stopId: Int,
        routeId: Int,
        lineShortCode: String,
    ): Result<Timetable?> {
        return try {
            Result.Success(
                data = httpClient.get("/routing/timetables") {
                    parameter("forDate", forDate)
                    parameter("after", after)
                    parameter("stopId", stopId)
                    parameter("routeId", routeId)
                    parameter("lineShortCode", lineShortCode)
                }
                    .body<TimetableDAO>()
                    .takeIf { it.departures.isNotEmpty() }
                    ?.let {
                        Timetable(
                            it.date,
                            lineShortCode,
                            it.departures
                        )
                    }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}