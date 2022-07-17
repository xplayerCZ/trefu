package cz.davidkurzica.trefu.ui.screens.timetables

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
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.FullScreenSelection
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.ui.components.form.TimetablesForm

enum class TimetablesFocusState {
    Stop,
    Line,
    None,
}

@Composable
fun FormScreen(
    uiState: TimetablesUiState.Form,
    onFormSubmit: () -> Unit,
    onFormClean: () -> Unit,
    onDirectionChange: (Direction) -> Unit,
    onLineChange: (Line) -> Unit,
    onStopChange: (Stop) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {
    TimetablesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->

        var focus by remember { mutableStateOf(TimetablesFocusState.None) }

        when (focus) {
            TimetablesFocusState.None -> TimetablesTopAppBarScreen(
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                onFormSubmit = onFormSubmit,
                uiState = uiState
            ) {
                TimetablesForm(
                    modifier = contentModifier,
                    directions = hasDataUiState.directions,
                    selectedStop = hasDataUiState.selectedStop,
                    selectedLine = hasDataUiState.selectedLine,
                    selectedDirection = hasDataUiState.selectedDirection,
                    onDirectionChange = onDirectionChange,
                    onFocusChange = { focus = it },
                )
            }
            TimetablesFocusState.Stop -> FullScreenSelection(
                options = hasDataUiState.stops,
                onSelectedChange = onStopChange,
                selectedOption = hasDataUiState.selectedStop,
                scaffoldState = scaffoldState,
                onCloseSelection = { focus = TimetablesFocusState.None },
                displayValue = { it.name },
            )
            TimetablesFocusState.Line -> FullScreenSelection(
                options = hasDataUiState.lines,
                onSelectedChange = onLineChange,
                selectedOption = hasDataUiState.selectedLine,
                scaffoldState = scaffoldState,
                onCloseSelection = { focus = TimetablesFocusState.None },
                displayValue = { it.shortCode },
            )
        }
    }
}

@Composable
private fun TimetablesScreenWithForm(
    uiState: TimetablesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: TimetablesUiState.Form.HasData,
        modifier: Modifier,
    ) -> Unit,
) {
    LoadingContent(
        empty = when (uiState) {
            is TimetablesUiState.Form.HasData -> false
            is TimetablesUiState.Form.NoData -> uiState.isLoading
        },
        emptyContent = {
            TimetablesScreenLoading(
                scaffoldState = scaffoldState,
                openDrawer = openDrawer,
                onFormSubmit = onFormSubmit,
                uiState = uiState,
            )
        },
        content = {
            when (uiState) {
                is TimetablesUiState.Form.HasData -> hasDataContent(
                    uiState,
                    modifier
                )
                is TimetablesUiState.Form.NoData -> Box(modifier.fillMaxSize()) { /* empty screen */ }
            }
        }
    )
}

@Composable
fun TimetablesScreenLoading(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: TimetablesUiState.Form,
) {
    TimetablesTopAppBarScreen(
        scaffoldState = scaffoldState,
        openDrawer = openDrawer,
        onFormSubmit = onFormSubmit,
        uiState = uiState
    ) {
        FullScreenLoading()
    }
}

@Composable
fun TimetablesTopAppBarScreen(
    scaffoldState: ScaffoldState,
    openDrawer: () -> Unit,
    onFormSubmit: () -> Unit,
    uiState: TimetablesUiState.Form,
    body: @Composable (Modifier) -> Unit,
) {
    val title = stringResource(id = R.string.timetables_title)

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
            if (uiState is TimetablesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = onFormSubmit,
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for timetables"
                    )
                }
            }
        },
    ) {
        body(Modifier.padding(it))
    }
}

