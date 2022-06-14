package cz.davidkurzica.trefu.data.tracks

import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

interface FormService {

    @GET("stop/item")
    suspend fun getStops(): List<Stop>

    @GET("line/item")
    suspend fun getLines(
        @Query("stopId") stopId: Int,
        @Query("date") date: LocalDate,
    ): List<Line>

    @GET("direction/item")
    suspend fun getDirections(
        @Query("lineId") lineId: Int,
    ): List<Direction>
}