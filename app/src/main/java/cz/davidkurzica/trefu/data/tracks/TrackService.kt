package cz.davidkurzica.trefu.data.tracks

import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.model.Track
import org.joda.time.LocalTime
import retrofit2.http.GET
import retrofit2.http.Path

interface TrackService {

    @GET("track")
    suspend fun getTracks(): List<Track>
}