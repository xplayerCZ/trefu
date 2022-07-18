package cz.davidkurzica.trefu

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.okHttpClient
import cz.davidkurzica.trefu.ui.TrefuApp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    lateinit var apolloClient: ApolloClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val connectTimeout = 10000L
        val readTimeout = 10000L

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .build()

        apolloClient = ApolloClient.Builder()
            .serverUrl("http://192.168.1.21:4000/graphql")
            .addHttpInterceptor(LoggingInterceptor(LoggingInterceptor.Level.BODY) {
                Log.i(
                    "ApolloClient",
                    it
                )
            })
            .okHttpClient(okHttpClient)
            .build()

        setContent {
            TrefuApp(apolloClient)
        }
    }
}