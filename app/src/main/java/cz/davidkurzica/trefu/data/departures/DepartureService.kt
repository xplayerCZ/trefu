package cz.davidkurzica.trefu.data.departures

import cz.davidkurzica.trefu.model.DepartureWithLine
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime

interface DepartureService {

    @GET("departure/item")
    suspend fun getDepartures(@Query("stopId") stopId: Int, @Query("time") time: LocalTime, @Query("date") date: LocalDate): List<DepartureWithLine>
}