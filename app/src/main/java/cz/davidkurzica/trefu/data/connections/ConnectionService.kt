package cz.davidkurzica.trefu.data.connections

import cz.davidkurzica.trefu.model.Connection
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime

interface ConnectionService {

    @GET("connection/item")
    suspend fun getConnections(
        @Query("fromStopId") fromStopId: Int,
        @Query("fromTime") fromTime: LocalTime,
        @Query("toStopId") toStopId: Int,
        @Query("toTime") toTime: LocalTime,
        @Query("date") date: LocalDate
    ): List<Connection>
}