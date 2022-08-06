package cz.davidkurzica.trefu.presentation.screens.departures

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.presentation.components.ErrorPage
import cz.davidkurzica.trefu.presentation.components.FullScreenLoading
import cz.davidkurzica.trefu.presentation.components.FullScreenSelection
import cz.davidkurzica.trefu.presentation.components.LoadingContent
import cz.davidkurzica.trefu.presentation.components.appbar.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.presentation.components.form.DeparturesForm
import java.time.LocalTime

enum class DeparturesFocusState {
    Stop,
    None,
}

@Composable
fun FormScreen(
    uiState: DeparturesUiState.Form,
    onFormSubmit: () -> Unit,
    onFormRefresh: () -> Unit,
    onStopChange: (StopOption) -> Unit,
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
        onFormRefresh = onFormRefresh,
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
    onFormRefresh: () -> Unit,
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
        loading = uiState.isLoading,
        onRefresh = { onFormRefresh() },
        content = {
            when (uiState) {
                is DeparturesUiState.Form.HasData -> hasDataContent(
                    uiState,
                    modifier
                )
                is DeparturesUiState.Form.NoData -> DeparturesTopAppBarScreen(
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
