package cz.davidkurzica.trefu.di

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.okHttpClient
import cz.davidkurzica.trefu.presentation.screens.connections.ConnectionsViewModel
import cz.davidkurzica.trefu.presentation.screens.departures.DeparturesViewModel
import cz.davidkurzica.trefu.presentation.screens.timetables.TimetablesViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        val connectTimeout = 10000L
        val readTimeout = 10000L

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .build()

        val apolloClient = ApolloClient.Builder()
            .serverUrl("https://trefu.davidkurzica.cz/graphql")
            .addHttpInterceptor(LoggingInterceptor(LoggingInterceptor.Level.BODY) {
                Log.i(
                    "ApolloClient",
                    it
                )
            })
            .okHttpClient(okHttpClient)
            .build()

        return@single apolloClient
    }

    single {
        val httpClient = HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json()
            }
            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "trefu.davidkurzica.cz"
                }
            }
            engine {
                config {
                    connectTimeout(10000, TimeUnit.MILLISECONDS)
                    readTimeout(10000, TimeUnit.MILLISECONDS)
                }
            }
        }

        httpClient
    }

    viewModelOf(::ConnectionsViewModel)
    viewModelOf(::DeparturesViewModel)
    viewModelOf(::TimetablesViewModel)
}