package cz.davidkurzica.trefu.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cz.davidkurzica.trefu.data.AppContainer
import cz.davidkurzica.trefu.data.AppContainerImpl

class MainActivity : ComponentActivity() {
    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = AppContainerImpl(this)

        setContent {
            TrefuApp(container)
        }
    }
}