package cz.davidkurzica.trefu.ui.departures

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable

@Composable
fun DeparturesRoute(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    DeparturesScreen(
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}