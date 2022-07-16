package cz.davidkurzica.trefu.ui.screens.connections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.ConnectionsFormData
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.ui.components.connections.ConnectionsForm

@Composable
fun FormScreen(
    uiState: ConnectionsUiState.Form,
    onFormSubmit: (ConnectionsFormData) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (ConnectionsFormData) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    ConnectionsScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->
        ConnectionsForm(
            modifier = contentModifier,
            formData = hasDataUiState.formData,
            onFormDataUpdate = onFormUpdate,
            options = hasDataUiState.stops
        )
    }
}

@Composable
private fun ConnectionsScreenWithForm(
    uiState: ConnectionsUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (ConnectionsFormData) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: ConnectionsUiState.Form.HasData,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.connections_title)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TrefuDefaultTopAppBar(
                title = title,
                iconDescription = stringResource(R.string.cd_open_navigation_drawer),
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            if (uiState is ConnectionsUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = {
                        onFormSubmit(uiState.formData)
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for connections"
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LoadingContent(
            empty = when (uiState) {
                is ConnectionsUiState.Form.HasData -> false
                is ConnectionsUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is ConnectionsUiState.Form.HasData -> {

                        hasDataContent(uiState, modifier.padding(padding))

                    }
                    is ConnectionsUiState.Form.NoData -> {
                        Box(modifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}