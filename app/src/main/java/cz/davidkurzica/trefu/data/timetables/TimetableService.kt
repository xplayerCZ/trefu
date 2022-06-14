package cz.davidkurzica.trefu.data.timetables

import cz.davidkurzica.trefu.model.Timetable
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

interface TimetableService {

    @GET("departure/timetable")
    suspend fun getTimetable(
        @Query("stopId") stopId: Int,
        @Query("lineId") lineId: Int,
        @Query("directionId") directionId: Int,
        @Query("date") date: LocalDate,
    ): Timetable
}