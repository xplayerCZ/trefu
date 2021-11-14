package cz.davidkurzica.trefu.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import kotlinx.coroutines.launch

@Composable
fun TrefuApp() {
    TrefuTheme {
            val navController = rememberNavController()
            val navigationActions = remember(navController) {
                TrefuNavigationActions(navController)
            }

            val coroutineScope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: TrefuDestinations.HOME_ROUTE

            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalDrawer(
                drawerContent = {
                    AppDrawer(
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToDepartures = navigationActions.navigateToDepartures,
                        navigateToConnections = navigationActions.navigateToConnections,
                        navigateToTimetables = navigationActions.navigateToTimetables,
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                    )
                },
                drawerState = drawerState,
                gesturesEnabled = true
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                ) {
                    TrefuNavGraph(
                        navController = navController,
                        openDrawer = { coroutineScope.launch { drawerState.open() } }
                    )
                }
            }
        }
}
