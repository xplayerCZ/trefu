package cz.davidkurzica.trefu.data.departures

import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.data.Result
import org.joda.time.LocalTime
import retrofit2.http.GET
import retrofit2.http.Query

interface DeparturesService {

    @GET("departure")
    suspend fun getDepartures(@Query("stopId") stopId: Int, @Query("time") time: LocalTime): List<Departure>
}