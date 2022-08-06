package cz.davidkurzica.trefu.di

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.okHttpClient
import cz.davidkurzica.trefu.presentation.screens.connections.ConnectionsViewModel
import cz.davidkurzica.trefu.presentation.screens.departures.DeparturesViewModel
import cz.davidkurzica.trefu.presentation.screens.timetables.TimetablesViewModel
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
            .serverUrl("http://192.168.1.21:4000/graphql")
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

    viewModelOf(::ConnectionsViewModel)
    viewModelOf(::DeparturesViewModel)
    viewModelOf(::TimetablesViewModel)
}