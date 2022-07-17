package cz.davidkurzica.trefu.ui.screens.connections

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.model.Stop
import java.time.LocalTime

@Composable
fun ConnectionsRoute(
    connectionsViewModel: ConnectionsViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val uiState by connectionsViewModel.uiState.collectAsState()

    ConnectionsRoute(
        uiState = uiState,
        onFormSubmit = { connectionsViewModel.submitForm() },
        onFormClean = { connectionsViewModel.cleanForm() },
        onStopFromChange = { connectionsViewModel.updateFromStop(it) },
        onStopToChange = { connectionsViewModel.updateToStop(it) },
        onTimeChange = { connectionsViewModel.updateTime(it) },
        onErrorDismiss = { connectionsViewModel.errorShown(it) },
        closeResults = { connectionsViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun ConnectionsRoute(
    uiState: ConnectionsUiState,
    onFormSubmit: () -> Unit,
    onFormClean: () -> Unit,
    onStopFromChange: (Stop) -> Unit,
    onStopToChange: (Stop) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
) {
    when (getConnectionsScreenType(uiState)) {
        ConnectionsScreenType.Form -> {
            FormScreen(
                uiState = uiState as ConnectionsUiState.Form,
                onFormSubmit = onFormSubmit,
                onFormClean = onFormClean,
                onStopFromChange = onStopFromChange,
                onStopToChange = onStopToChange,
                onTimeChange = onTimeChange,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
            )
        }
        ConnectionsScreenType.Results -> {
            ResultsScreen(
                uiState = uiState as ConnectionsUiState.Results,
                onErrorDismiss = onErrorDismiss,
                closeResults = closeResults,
                scaffoldState = scaffoldState,
            )

            BackHandler {
                closeResults()
            }
        }
    }
}

private enum class ConnectionsScreenType {
    Form,
    Results,
}

@Composable
private fun getConnectionsScreenType(
    uiState: ConnectionsUiState,
): ConnectionsScreenType = when (uiState.isResultsOpen) {
    false -> ConnectionsScreenType.Form
    true -> ConnectionsScreenType.Results
}