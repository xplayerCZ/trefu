package cz.davidkurzica.trefu.ui.departures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.Time
import cz.davidkurzica.trefu.ui.components.TrefuSnackbarHost
import cz.davidkurzica.trefu.ui.components.TrefuTimePickerDialog
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun FormScreen(
    uiState: DeparturesUiState.Form,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (Stop) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    DeparturesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->
        DeparturesForm(
            modifier = contentModifier,
            selectedStop = hasDataUiState.selectedStop,
            onSelectedTrackChange = onFormUpdate,
            options = hasDataUiState.stops
        )
    }
}


@Composable
fun ResultsScreen(
    uiState: DeparturesUiState.Results,
    openDrawer: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    DeparturesScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasDeparturesUiState, contentModifier ->
        DeparturesList(
            departures = hasDeparturesUiState.departures,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun DeparturesScreenWithList(
    uiState: DeparturesUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDeparturesContent: @Composable (
        uiState: DeparturesUiState.Results.HasResults,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.departures_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            DeparturesTopAppBar(
                title = title,
                openDrawer = openDrawer,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is DeparturesUiState.Results.HasResults -> false
                is DeparturesUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = { },
            content = {
                when (uiState) {
                    is DeparturesUiState.Results.HasResults -> hasDeparturesContent(
                        uiState,
                        contentModifier
                    )
                    is DeparturesUiState.Results.NoResults -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}

@Composable
private fun DeparturesScreenWithForm(
    uiState: DeparturesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (Int, LocalTime) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: DeparturesUiState.Form.HasData,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.departures_title)

    LoadingContent(
        empty = when (uiState) {
            is DeparturesUiState.Form.HasData -> false
            is DeparturesUiState.Form.NoData -> uiState.isLoading
        },
        emptyContent = { FullScreenLoading() },
        loading = uiState.isLoading,
        onRefresh = { },
        content = {
            when (uiState) {
                is DeparturesUiState.Form.HasData -> {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            DeparturesTopAppBar(
                                title = title,
                                openDrawer = openDrawer
                            )
                        },
                        floatingActionButton = {
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
                                    contentDescription = "Search for departures"
                                )
                            }
                        },
                        modifier = modifier
                    ) {
                        hasDataContent(uiState, modifier)
                    }
                }
                is DeparturesUiState.Form.NoData -> {
                    Box(modifier.fillMaxSize()) { /* empty screen */ }
                }
            }
        }
    )
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}

@Composable
fun DeparturesForm(
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
            DeparturesStopLocation(
                selectedStop = selectedStop,
                onSelectedTrackChange = onSelectedTrackChange,
                options
            )
        }
    }
}

@Composable
fun DeparturesList(
    modifier: Modifier = Modifier,
    departures: List<Departure>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items = departures) { _, item ->
            DepartureItem(departure = item)
        }
    }
}

@Composable
fun DepartureItem(
    departure: Departure
) {
    Card(
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                Text(
                    text = formatter.format(departure.time),
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = departure.lineShortCode,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "konečná: ${departure.lastStopName}",
                fontSize = MaterialTheme.typography.caption.fontSize
            )
        }
    }
}

@Preview("DepartureItem Preview")
@Composable
fun DepartureItemPreview() {
    TrefuTheme {
        DepartureItem(departure = Departure(LocalTime.of(18, 30), "208", "Malé Hoštice"))
    }
}


@Composable
fun DeparturesTime() {
    val openDialog = remember { mutableStateOf(false) }
    val (time, setTime) = remember { mutableStateOf(Time(12, 0)) }

    TextButton(
        onClick = { openDialog.value = true }
    ) {
        val text = "%02d %02d".format(time.hours, time.minutes)
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.primary
        )
    }

    if (openDialog.value) {
        TrefuTimePickerDialog(
            time = time,
            onTimeSelected = setTime,
            onDismissRequest = { openDialog.value = false }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeparturesStopLocation(
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
            label = { Text("Label") },
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
fun DeparturesTopAppBar(
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
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}