package cz.davidkurzica.trefu.ui.screens.home

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    HomeScreen(
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}