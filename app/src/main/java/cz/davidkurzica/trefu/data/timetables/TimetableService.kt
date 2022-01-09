package cz.davidkurzica.trefu.data.timetables

import cz.davidkurzica.trefu.model.DepartureWithLine
import cz.davidkurzica.trefu.model.Timetable
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime

interface TimetableService {

    @GET("timetable/item")
    suspend fun getTimetable(@Query("stopId") stopId: Int, @Query("time") time: LocalTime, @Query("date") date: LocalDate): Timetable
}