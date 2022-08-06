package cz.davidkurzica.trefu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cz.davidkurzica.trefu.di.appModule
import cz.davidkurzica.trefu.di.repositoryModule
import cz.davidkurzica.trefu.presentation.TrefuApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule, repositoryModule)
        }

        setContent {
            TrefuApp()
        }
    }
}