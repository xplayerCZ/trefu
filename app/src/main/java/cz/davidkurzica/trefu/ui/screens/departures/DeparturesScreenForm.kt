package cz.davidkurzica.trefu.ui.screens.departures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.FullScreenSelection
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.ui.components.form.DeparturesForm
import java.time.LocalTime

enum class DeparturesFocusState {
    Stop,
    None,
}

@Composable
fun FormScreen(
    uiState: DeparturesUiState.Form,
    onFormSubmit: () -> Unit,
    onFormClean: () -> Unit,
    onStopChange: (Stop) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {
    DeparturesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->

        var focus by remember { mutableStateOf(DeparturesFocusState.None) }

        when (focus) {
            DeparturesFocusState.None -> DeparturesTopAppBarScreen(
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                onFormSubmit = onFormSubmit,
                uiState = uiState
            ) {
                DeparturesForm(
                    modifier = contentModifier,
                    selectedStop = hasDataUiState.selectedStop,
                    selectedTime = hasDataUiState.selectedTime,
                    onTimeChange = onTimeChange,
                    onFocusChange = { focus = it }
                )
            }
            DeparturesFocusState.Stop -> FullScreenSelection(
                scaffoldState = scaffoldState,
                options = hasDataUiState.stops,
                selectedOption = hasDataUiState.selectedStop,
                displayValue = { it.name },
                onCloseSelection = { focus = DeparturesFocusState.None },
                onSelectedChange = onStopChange,
            )
        }
    }
}

@Composable
private fun DeparturesScreenWithForm(
    uiState: DeparturesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: DeparturesUiState.Form.HasData,
        modifier: Modifier,
    ) -> Unit,
) {
    LoadingContent(
        empty = when (uiState) {
            is DeparturesUiState.Form.HasData -> false
            is DeparturesUiState.Form.NoData -> uiState.isLoading
        },
        emptyContent = {
            DeparturesScreenLoading(
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                onFormSubmit = onFormSubmit,
                uiState = uiState,
            )
        },
        content = {
            when (uiState) {
                is DeparturesUiState.Form.HasData -> hasDataContent(
                    uiState,
                    modifier
                )
                is DeparturesUiState.Form.NoData -> Box(modifier.fillMaxSize()) { /* empty screen */ }
            }
        }
    )
}

@Composable
fun DeparturesScreenLoading(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: DeparturesUiState.Form,
) {
    DeparturesTopAppBarScreen(
        scaffoldState = scaffoldState,
        openDrawer = openDrawer,
        onFormSubmit = onFormSubmit,
        uiState = uiState
    ) {
        FullScreenLoading()
    }
}

@Composable
fun DeparturesTopAppBarScreen(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: DeparturesUiState.Form,
    body: @Composable (Modifier) -> Unit,
) {
    val title = stringResource(id = R.string.departures_title)

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
            if (uiState is DeparturesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = onFormSubmit,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for departures"
                    )
                }
            }
        },
    ) {
        body(Modifier.padding(it))
    }
}
