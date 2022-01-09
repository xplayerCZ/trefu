package cz.davidkurzica.trefu.data.tracks

import cz.davidkurzica.trefu.model.Stop
import retrofit2.http.GET

interface StopService {

    @GET("stop/item")
    suspend fun getStops(): List<Stop>
}