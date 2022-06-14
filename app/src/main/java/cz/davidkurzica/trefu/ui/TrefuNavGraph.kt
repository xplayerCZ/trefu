package cz.davidkurzica.trefu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.davidkurzica.trefu.data.AppContainer
import cz.davidkurzica.trefu.ui.screens.connections.ConnectionsRoute
import cz.davidkurzica.trefu.ui.screens.connections.ConnectionsViewModel
import cz.davidkurzica.trefu.ui.screens.departures.DeparturesRoute
import cz.davidkurzica.trefu.ui.screens.departures.DeparturesViewModel
import cz.davidkurzica.trefu.ui.screens.home.HomeRoute
import cz.davidkurzica.trefu.ui.screens.timetables.TimetablesRoute
import cz.davidkurzica.trefu.ui.screens.timetables.TimetablesViewModel

@Composable
fun TrefuNavGraph(
    appContainer: AppContainer,
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
            val departuresViewModel: DeparturesViewModel = viewModel(
                factory = DeparturesViewModel.provideFactory(
                    appContainer.departureService,
                    appContainer.formService
                )
            )

            DeparturesRoute(
                departuresViewModel = departuresViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.CONNECTIONS_ROUTE) {
            val connectionsViewModel: ConnectionsViewModel = viewModel(
                factory = ConnectionsViewModel.provideFactory(
                    appContainer.connectionService,
                    appContainer.formService
                )
            )

            ConnectionsRoute(
                connectionsViewModel = connectionsViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.TIMETABLES_ROUTE) {
            val timetablesViewModel: TimetablesViewModel = viewModel(
                factory = TimetablesViewModel.provideFactory(
                    appContainer.timetableService,
                    appContainer.formService
                )
            )

            TimetablesRoute(
                timetablesViewModel = timetablesViewModel,
                openDrawer = openDrawer
            )
        }
    }
}
