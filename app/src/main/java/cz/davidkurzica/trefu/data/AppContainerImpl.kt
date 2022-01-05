package cz.davidkurzica.trefu.data

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import cz.davidkurzica.trefu.data.departures.DeparturesService
import cz.davidkurzica.trefu.data.tracks.StopService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit


interface AppContainer {
    val departuresService: DeparturesService
    val stopService: StopService
}


@OptIn(ExperimentalSerializationApi::class)
class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    private val contentType = MediaType.parse("application/json; charset=utf-8")!!

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.20:8080/")
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()

    override val departuresService: DeparturesService = retrofit.create(DeparturesService::class.java)
    override val stopService: StopService = retrofit.create(StopService::class.java)
}