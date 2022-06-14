package cz.davidkurzica.trefu.ui.screens.connections

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.model.ConnectionsFormData

@Composable
fun ConnectionsRoute(
    connectionsViewModel: ConnectionsViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by connectionsViewModel.uiState.collectAsState()

    ConnectionsRoute(
        uiState = uiState,
        onFormSubmit = { connectionsViewModel.submitForm(it) },
        onFormUpdate = { connectionsViewModel.updateForm(it) },
        onFormClean = { connectionsViewModel.cleanForm() },
        onErrorDismiss = { connectionsViewModel.errorShown(it) },
        closeResults = { connectionsViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun ConnectionsRoute(
    uiState: ConnectionsUiState,
    onFormSubmit: (ConnectionsFormData) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (ConnectionsFormData) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val connectionsScreenType = getConnectionsScreenType(uiState)
    when (connectionsScreenType) {
        ConnectionsScreenType.Form -> {
            FormScreen(
                uiState = uiState as ConnectionsUiState.Form,
                onFormSubmit = onFormSubmit,
                onFormClean = onFormClean,
                onFormUpdate = onFormUpdate,
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
    uiState: ConnectionsUiState
): ConnectionsScreenType = when (uiState.isResultsOpen) {
    false -> ConnectionsScreenType.Form
    true -> ConnectionsScreenType.Results
}