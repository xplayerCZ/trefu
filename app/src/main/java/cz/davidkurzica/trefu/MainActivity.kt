package cz.davidkurzica.trefu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cz.davidkurzica.trefu.ui.TrefuApp
import cz.davidkurzica.trefu.utils.rememberWindowSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowSizeClass = rememberWindowSizeClass()
            TrefuApp(windowSizeClass)
        }
    }
}