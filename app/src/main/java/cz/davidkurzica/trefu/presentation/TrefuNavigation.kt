package cz.davidkurzica.trefu.presentation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Destinations used in the [TrefuApp].
 */
object TrefuDestinations {
    const val HOME_ROUTE = "home"
    const val DEPARTURES_ROUTE = "departures"
    const val CONNECTIONS_ROUTE = "connections"
    const val TIMETABLES_ROUTE = "timetables"
}

/**
 * Models the navigation actions in the app.
 */
class TrefuNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(TrefuDestinations.HOME_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false //DEFAULT: true
            }
            launchSingleTop = true
            restoreState = false //DEFAULT: true
        }
    }
    val navigateToDepartures: () -> Unit = {
        navController.navigate(TrefuDestinations.DEPARTURES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false //DEFAULT: true
            }
            launchSingleTop = true
            restoreState = false //DEFAULT: true
        }
    }
    val navigateToConnections: () -> Unit = {
        navController.navigate(TrefuDestinations.CONNECTIONS_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false //DEFAULT: true
            }
            launchSingleTop = true
            restoreState = false //DEFAULT: true
        }
    }
    val navigateToTimetables: () -> Unit = {
        navController.navigate(TrefuDestinations.TIMETABLES_ROUTE) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false //DEFAULT: true
            }
            launchSingleTop = true
            restoreState = false //DEFAULT: true
        }
    }
}
