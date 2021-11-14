package cz.davidkurzica.trefu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.davidkurzica.trefu.ui.home.HomeRoute

@Composable
fun TrefuNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = TrefuDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(TrefuDestinations.HOME_ROUTE) {
            HomeRoute(openDrawer = openDrawer)
        }
        composable(TrefuDestinations.DEPARTURES_ROUTE) {

        }
        composable(TrefuDestinations.CONNECTIONS_ROUTE) {

        }
        composable(TrefuDestinations.TIMETABLES_ROUTE) {

        }
    }
}
