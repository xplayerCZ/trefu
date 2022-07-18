package cz.davidkurzica.trefu.ui.screens.connections

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.ErrorPage
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.FullScreenSelection
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.appbar.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.ui.components.form.ConnectionsForm
import java.time.LocalTime

enum class ConnectionsFocusState {
    StopFrom,
    StopTo,
    None,
}

@Composable
fun FormScreen(
    uiState: ConnectionsUiState.Form,
    onFormSubmit: () -> Unit,
    onFormRefresh: () -> Unit,
    onStopFromChange: (Stop) -> Unit,
    onStopToChange: (Stop) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {
    ConnectionsScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        onFormRefresh = onFormRefresh,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->

        var focus by remember { mutableStateOf(ConnectionsFocusState.None) }

        when (focus) {
            ConnectionsFocusState.None -> ConnectionsTopAppBarScreen(
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                onFormSubmit = onFormSubmit,
                uiState = uiState
            ) {
                ConnectionsForm(
                    modifier = contentModifier,
                    selectedFromStop = hasDataUiState.selectedFromStop,
                    selectedToStop = hasDataUiState.selectedToStop,
                    selectedTime = hasDataUiState.selectedTime,
                    onTimeChange = onTimeChange,
                    onFocusChange = { focus = it },
                )
            }
            ConnectionsFocusState.StopFrom -> FullScreenSelection(
                options = hasDataUiState.stops,
                onSelectedChange = onStopFromChange,
                selectedOption = hasDataUiState.selectedFromStop,
                scaffoldState = scaffoldState,
                onCloseSelection = { focus = ConnectionsFocusState.None },
                displayValue = { it.name },
            )
            ConnectionsFocusState.StopTo -> FullScreenSelection(
                options = hasDataUiState.stops,
                onSelectedChange = onStopToChange,
                selectedOption = hasDataUiState.selectedToStop,
                scaffoldState = scaffoldState,
                onCloseSelection = { focus = ConnectionsFocusState.None },
                displayValue = { it.name },
            )
        }
    }
}

@Composable
private fun ConnectionsScreenWithForm(
    uiState: ConnectionsUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: () -> Unit,
    onFormRefresh: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: ConnectionsUiState.Form.HasData,
        modifier: Modifier,
    ) -> Unit,
) {
    LoadingContent(
        empty = when (uiState) {
            is ConnectionsUiState.Form.HasData -> false
            is ConnectionsUiState.Form.NoData -> uiState.isLoading
        },
        emptyContent = {
            ConnectionsScreenLoading(
                uiState = uiState,
                scaffoldState = scaffoldState,
                onFormSubmit = onFormSubmit,
                openDrawer = openDrawer,
            )
        },
        loading = uiState.isLoading,
        onRefresh = { onFormRefresh() },
        content = {
            when (uiState) {
                is ConnectionsUiState.Form.HasData -> hasDataContent(uiState, modifier)
                is ConnectionsUiState.Form.NoData -> ConnectionsTopAppBarScreen(
                    scaffoldState = scaffoldState,
                    openDrawer = openDrawer,
                    onFormSubmit = onFormSubmit,
                    uiState = uiState
                ) {
                    ErrorPage(onErrorDismiss = onErrorDismiss)
                }
            }
        }
    )
}

@Composable
fun ConnectionsScreenLoading(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: ConnectionsUiState.Form,
) {
    ConnectionsTopAppBarScreen(
        scaffoldState = scaffoldState,
        openDrawer = openDrawer,
        onFormSubmit = onFormSubmit,
        uiState = uiState
    ) {
        FullScreenLoading()
    }
}

@Composable
fun ConnectionsTopAppBarScreen(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: ConnectionsUiState.Form,
    body: @Composable (Modifier) -> Unit,
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
                    onClick = onFormSubmit,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for connections"
                    )
                }
            }
        },
    ) {
        body(Modifier.padding(it))
    }
}
