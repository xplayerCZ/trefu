package cz.davidkurzica.trefu.ui.timetables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.DepartureSimple
import cz.davidkurzica.trefu.model.Timetable
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.TrefuSnackbarHost
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun FormScreen(
    uiState: TimetablesUiState.Form,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (Stop) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    TimetablesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->
        TimetablesForm(
            modifier = contentModifier,
            selectedStop = hasDataUiState.selectedStop,
            onSelectedTrackChange = onFormUpdate,
            options = hasDataUiState.stops
        )
    }
}


@Composable
fun ResultsScreen(
    uiState: TimetablesUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    TimetablesScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        closeResults = closeResults,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasTimetablesUiState, contentModifier ->
        TimetablesList(
            timetable = hasTimetablesUiState.timetables,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun TimetablesScreenWithList(
    uiState: TimetablesUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasTimetablesContent: @Composable (
        uiState: TimetablesUiState.Results.HasResults,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.timetables_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            TimetablesResultsTopAppBar(
                title = title,
                closeResults = closeResults,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is TimetablesUiState.Results.HasResults -> false
                is TimetablesUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is TimetablesUiState.Results.HasResults -> hasTimetablesContent(
                        uiState,
                        contentModifier
                    )
                    is TimetablesUiState.Results.NoResults -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}

@Composable
private fun TimetablesScreenWithForm(
    uiState: TimetablesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (Int, LocalTime) -> Unit,
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
            if(uiState is TimetablesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = {
                        onFormSubmit(
                            uiState.selectedStop.id,
                            LocalTime.now()
                        )
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
    ) {
        LoadingContent(
            empty = when (uiState) {
                is TimetablesUiState.Form.HasData -> false
                is TimetablesUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is TimetablesUiState.Form.HasData -> {

                        hasDataContent(uiState, modifier)

                    }
                    is TimetablesUiState.Form.NoData -> {
                        Box(modifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        content()
    }
}

@Composable
fun TimetablesForm(
    modifier: Modifier = Modifier,
    options: List<Stop>,
    selectedStop: Stop,
    onSelectedTrackChange: (Stop) -> Unit
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            TimetablesStopLocation(
                selectedStop = selectedStop,
                onSelectedTrackChange = onSelectedTrackChange,
                options
            )
        }
    }
}

@Composable
fun TimetablesList(
    modifier: Modifier = Modifier,
    timetable: Timetable
) {
    Column {
        Row {
            Text(
                text = timetable.lineShortCode,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = timetable.date.toString(),
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(items = timetable.departures) { _, item ->
                TimetableItem(timetable = item)
            }
        }
    }
}

@Composable
fun TimetableItem(
    timetable: DepartureSimple
) {
    Card(
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            Text(
                text = formatter.format(timetable.time),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "konečná: ${timetable.stopName}",
                fontSize = MaterialTheme.typography.caption.fontSize
            )
        }
    }
}

@Preview("TimetableItem Preview")
@Composable
fun TimetableItemPreview() {
    TrefuTheme {
        TimetableItem(
            DepartureSimple(LocalTime.now().plusMinutes(5), "end")
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetablesStopLocation(
    selectedStop: Stop,
    onSelectedTrackChange: (Stop) -> Unit,
    options: List<Stop>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedStop.name,
            onValueChange = { },
            label = { Text("Stop") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onSelectedTrackChange(item)
                        expanded = false
                    },
                    enabled = item.enabled
                ) {
                    Text(text = item.name)
                }
            }
        }
    }
}


@Composable
fun TimetablesFormTopAppBar(
    title: String,
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                )
            }
        },
        elevation = 0.dp
    )
}

@Composable
fun TimetablesResultsTopAppBar(
    title: String,
    closeResults: () -> Unit
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = closeResults) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_close_timetables_results),
                )
            }
        },
        elevation = 0.dp
    )
}
