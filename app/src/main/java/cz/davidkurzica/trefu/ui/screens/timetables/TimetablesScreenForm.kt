package cz.davidkurzica.trefu.ui.screens.timetables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.TimetablesFormData
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.timetables.TimetablesForm
import cz.davidkurzica.trefu.ui.components.timetables.TimetablesFormTopAppBar

enum class FocusStates {
    Stop,
    Line,
    None,
}

@Composable
fun FormScreen(
    uiState: TimetablesUiState.Form,
    onFormSubmit: (TimetablesFormData) -> Unit,
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

        var focus by remember { mutableStateOf(FocusStates.None) }

        when (focus) {
            FocusStates.None -> TimetablesForm(
                modifier = contentModifier,
                formData = hasDataUiState.formData,
                onLineChange = onLineChange,
                onStopChange = onStopChange,
                onDirectionChange = onDirectionChange,
                onFocusChange = { focus = it },
                stops = hasDataUiState.stops,
                lines = hasDataUiState.lines,
                directions = hasDataUiState.directions
            )
            FocusStates.Stop -> StopSelection(scaffoldState) { focus = FocusStates.None }
            FocusStates.Line -> LineSelection(
                lines = hasDataUiState.lines,
                onLineChange = onLineChange,
                selectedLine = hasDataUiState.formData.selectedLine,
                scaffoldState = scaffoldState
            ) { focus = FocusStates.None }
        }
    }
}

@Composable
private fun TimetablesScreenWithForm(
    uiState: TimetablesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (TimetablesFormData) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: TimetablesUiState.Form.HasData,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.timetables_title)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TimetablesFormTopAppBar(
                title = title,
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            if (uiState is TimetablesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = {
                        onFormSubmit(uiState.formData)
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for timetables"
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LoadingContent(
            empty = when (uiState) {
                is TimetablesUiState.Form.HasData -> false
                is TimetablesUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is TimetablesUiState.Form.HasData -> hasDataContent(
                        uiState,
                        modifier.padding(padding)
                    )
                    is TimetablesUiState.Form.NoData -> Box(modifier.fillMaxSize()) { /* empty screen */ }
                }
            }
        )
    }
}

@Composable
private fun StopSelection(
    scaffoldState: ScaffoldState,
    onCloseSelection: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("Select stop")
                },
                navigationIcon = {
                    IconButton(onClick = onCloseSelection) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Close Selection",
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { padding ->
        Text(
            "Stop selection",
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun LineSelection(
    lines: List<Line>,
    onLineChange: (Line) -> Unit,
    selectedLine: Line,
    scaffoldState: ScaffoldState,
    onCloseSelection: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("Select line")
                },
                navigationIcon = {
                    IconButton(onClick = onCloseSelection) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Close Selection",
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(lines) {
                LineRow(
                    selected = it == selectedLine,
                    line = it,
                    onLineChange = onLineChange
                )
            }
        }
    }
}

@Composable
private fun LineRow(
    selected: Boolean,
    line: Line,
    onLineChange: (Line) -> Unit,
) {
    Text(
        text = line.shortCode,
        modifier = Modifier
            .border(1.dp, Color.Red)
            .padding(12.dp)
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onLineChange(line) }
            ))
}

@Composable
private fun DirectionSelection(
    scaffoldState: ScaffoldState,
    onCloseSelection: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("Select direction")
                },
                navigationIcon = {
                    IconButton(onClick = onCloseSelection) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Close Selection",
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { padding ->
        Text(
            "Direction selection",
            modifier = Modifier.padding(padding),
        )
    }
}
