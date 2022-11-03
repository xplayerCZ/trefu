package cz.davidkurzica.trefu.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.davidkurzica.trefu.presentation.screens.connections.ConnectionsRoute
import cz.davidkurzica.trefu.presentation.screens.connections.ConnectionsViewModel
import cz.davidkurzica.trefu.presentation.screens.departures.DeparturesRoute
import cz.davidkurzica.trefu.presentation.screens.departures.DeparturesViewModel
import cz.davidkurzica.trefu.presentation.screens.home.HomeRoute
import cz.davidkurzica.trefu.presentation.screens.timetables.TimetablesRoute
import cz.davidkurzica.trefu.presentation.screens.timetables.TimetablesViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun TrefuNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = TrefuDestinations.HOME_ROUTE,
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
            val departuresViewModel: DeparturesViewModel = getViewModel()

            DeparturesRoute(
                departuresViewModel = departuresViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.CONNECTIONS_ROUTE) {
            val connectionsViewModel: ConnectionsViewModel = getViewModel()

            ConnectionsRoute(
                connectionsViewModel = connectionsViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.TIMETABLES_ROUTE) {
            val timetablesViewModel: TimetablesViewModel = getViewModel()

            TimetablesRoute(
                timetablesViewModel = timetablesViewModel,
                openDrawer = openDrawer
            )
        }
    }
}
