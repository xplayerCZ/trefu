package cz.davidkurzica.trefu.ui.home

import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cz.davidkurzica.trefu.ui.AppDrawer
import cz.davidkurzica.trefu.ui.TrefuDestinations
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

@Composable
fun HomeRoute(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    HomeScreen(
        openDrawer = openDrawer,
        scaffoldState = scaffoldState
    )
}

@Preview("Home Route")
@Composable
fun HomeRoutePreview() {
    TrefuTheme {
        Surface {
            HomeRoute(openDrawer = { })
        }
    }
}