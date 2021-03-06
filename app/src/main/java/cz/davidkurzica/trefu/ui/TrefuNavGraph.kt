package cz.davidkurzica.trefu.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.ui.screens.connections.ConnectionsRoute
import cz.davidkurzica.trefu.ui.screens.connections.ConnectionsViewModel
import cz.davidkurzica.trefu.ui.screens.departures.DeparturesRoute
import cz.davidkurzica.trefu.ui.screens.departures.DeparturesViewModel
import cz.davidkurzica.trefu.ui.screens.home.HomeRoute
import cz.davidkurzica.trefu.ui.screens.timetables.TimetablesRoute
import cz.davidkurzica.trefu.ui.screens.timetables.TimetablesViewModel

@Composable
fun TrefuNavGraph(
    apolloClient: ApolloClient,
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
                factory = DeparturesViewModel.provideFactory(apolloClient)
            )

            DeparturesRoute(
                departuresViewModel = departuresViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.CONNECTIONS_ROUTE) {
            val connectionsViewModel: ConnectionsViewModel = viewModel(
                factory = ConnectionsViewModel.provideFactory(apolloClient)
            )

            ConnectionsRoute(
                connectionsViewModel = connectionsViewModel,
                openDrawer = openDrawer
            )
        }
        composable(TrefuDestinations.TIMETABLES_ROUTE) {
            val timetablesViewModel: TimetablesViewModel = viewModel(
                factory = TimetablesViewModel.provideFactory(apolloClient)
            )

            TimetablesRoute(
                timetablesViewModel = timetablesViewModel,
                openDrawer = openDrawer
            )
        }
    }
}
