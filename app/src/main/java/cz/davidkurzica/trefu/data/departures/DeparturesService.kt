package cz.davidkurzica.trefu.data.departures

import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.data.Result
import org.joda.time.LocalTime
import retrofit2.http.GET
import retrofit2.http.Path

interface DeparturesService {

    @GET("departure?stopId={stopId}&time={time}")
    suspend fun getDepartures(@Path("stopId") stopId: Int, @Path("time") time: LocalTime): Result<List<Departure>>
}