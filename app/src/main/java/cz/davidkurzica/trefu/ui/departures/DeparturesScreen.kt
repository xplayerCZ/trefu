package cz.davidkurzica.trefu.ui.departures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.model.DeparturesForm
import cz.davidkurzica.trefu.model.Track
import cz.davidkurzica.trefu.ui.components.Time
import cz.davidkurzica.trefu.ui.components.TrefuSnackbarHost
import cz.davidkurzica.trefu.ui.components.TrefuTimePickerDialog
import org.joda.time.LocalTime
import java.util.*

@Composable
fun FormScreen(
    uiState: DeparturesUiState,
    onSubmitForm: (Int, LocalTime) -> Unit,
    onCleanForm: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    when(uiState) {
        is DeparturesUiState.HasDepartures -> {
            val data = uiState.form
            var selectedTrack by remember { mutableStateOf(data.trackOptions[0]) }
            val title = stringResource(id = R.string.departures_title)

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
                        onClick = { onSubmitForm(selectedTrack.id, LocalTime.now()) },
                        backgroundColor = MaterialTheme.colors.primary
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "Search for departures")
                    }
                }
            ) { innerPadding ->
                val screenModifier = Modifier.padding(innerPadding)

                DeparturesForm(
                    modifier = screenModifier,
                    selectedTrack = selectedTrack,
                    onSelectedTrackChange = { selectedTrack = it},
                    options = data.trackOptions
                )
            }
        }
        is DeparturesUiState.NoDepartures -> {
            Box(modifier.fillMaxSize()) { /* empty screen */ }
        }
    }

}


@Composable
fun ResultsScreen(
    uiState: DeparturesUiState,
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
    uiState: DeparturesUiState,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDeparturesContent: @Composable (
        uiState: DeparturesUiState.HasDepartures,
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
                is DeparturesUiState.HasDepartures -> false
                is DeparturesUiState.NoDepartures -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = { },
            content = {
                when (uiState) {
                    is DeparturesUiState.HasDepartures -> hasDeparturesContent(uiState, contentModifier)
                    is DeparturesUiState.NoDepartures -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
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
    options: List<Track>,
    selectedTrack: Track,
    onSelectedTrackChange: (Track) -> Unit
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            DeparturesStopLocation(
                selectedTrack = selectedTrack,
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
        modifier = modifier
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
    Column {
        Row {
            Text(text = departure.time.toString("HH:mm"))
            Text(text = departure.lineNumber.toString())
        }
        Text(text = departure.finalStop)
    }
}

@Preview("DepartureItem Preview")
@Composable
fun DepartureItemPreview() {
    DepartureItem(departure = Departure(LocalTime.now(), 208,  "Malé Hoštice"))
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
    selectedTrack: Track,
    onSelectedTrackChange:  (Track) -> Unit,
    options: List<Track>
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
            value = selectedTrack.name,
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
            LazyColumn {
                itemsIndexed(items = options) { _, item ->
                    Box(
                        modifier = Modifier
                            .size(80.dp, 20.dp)
                            .wrapContentSize(Alignment.Center)
                    ) {
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