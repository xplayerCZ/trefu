package cz.davidkurzica.trefu.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor

class MainActivity : ComponentActivity() {
    lateinit var apolloClient: ApolloClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apolloClient = ApolloClient.Builder()
            .serverUrl("http://192.168.1.21:4000/graphql")
            .addHttpInterceptor(LoggingInterceptor(LoggingInterceptor.Level.BODY) {
                Log.i(
                    "ApolloClient",
                    it
                )
            })
            .build()

        setContent {
            TrefuApp(apolloClient)
        }
    }
}