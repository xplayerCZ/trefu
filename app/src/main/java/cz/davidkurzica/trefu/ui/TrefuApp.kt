package cz.davidkurzica.trefu.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import cz.davidkurzica.trefu.ui.components.AppNavRail
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import cz.davidkurzica.trefu.utils.WindowSize
import kotlinx.coroutines.launch

@Composable
fun TrefuApp(
    windowSize: WindowSize
) {
    TrefuTheme {
        ProvideWindowInsets {
            val systemUiController = rememberSystemUiController()
            val darkIcons = MaterialTheme.colors.isLight
            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = darkIcons)
            }

            val navController = rememberNavController()
            val navigationActions = remember(navController) {
                TrefuNavigationActions(navController)
            }

            val coroutineScope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: TrefuDestinations.HOME_ROUTE

            val isExpandedScreen = windowSize == WindowSize.Expanded
            val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

            ModalDrawer(
                drawerContent = {
                    AppDrawer(
                        currentRoute = currentRoute,
                        navigateToHome = navigationActions.navigateToHome,
                        navigateToInterests = navigationActions.navigateToInterests,
                        closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } },
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                },
                drawerState = sizeAwareDrawerState,
                // Only enable opening the drawer via gestures if the screen is not expanded
                gesturesEnabled = !isExpandedScreen
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(bottom = false)
                ) {
                    if (isExpandedScreen) {
                        AppNavRail(
                            currentRoute = currentRoute,
                            navigateToHome = navigationActions.navigateToHome,
                            navigateToInterests = navigationActions.navigateToInterests,
                        )
                    }
                    TrefuNavGraph(
                        isExpandedScreen = isExpandedScreen,
                        navController = navController,
                        openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } },
                    )
                }
            }
        }
    }
}

/**
 * Determine the drawer state to pass to the modal drawer.
 */
@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        // If we want to allow showing the drawer, we use a real, remembered drawer
        // state defined above
        drawerState
    } else {
        // If we don't want to allow the drawer to be shown, we provide a drawer state
        // that is locked closed. This is intentionally not remembered, because we
        // don't want to keep track of any changes and always keep it closed
        DrawerState(DrawerValue.Closed)
    }
}

/**
 * Determine the content padding to apply to the different screens of the app
 */
@Composable
fun rememberContentPaddingForScreen(additionalTop: Dp = 0.dp) =
    rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.systemBars,
        applyTop = false,
        applyEnd = false,
        applyStart = false,
        additionalTop = additionalTop
    )
