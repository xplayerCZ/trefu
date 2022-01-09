package cz.davidkurzica.trefu.ui.connections

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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.*
import cz.davidkurzica.trefu.ui.components.*
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
fun ResultsScreen(
    uiState: ConnectionsUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    ConnectionsScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        closeResults = closeResults,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasConnectionsUiState, contentModifier ->
        ConnectionsList(
            connections = hasConnectionsUiState.connections,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun ConnectionsScreenWithList(
    uiState: ConnectionsUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasConnectionsContent: @Composable (
        uiState: ConnectionsUiState.Results.HasResults,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.connections_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            ConnectionsResultsTopAppBar(
                title = title,
                closeResults = closeResults,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is ConnectionsUiState.Results.HasResults -> false
                is ConnectionsUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is ConnectionsUiState.Results.HasResults -> hasConnectionsContent(
                        uiState,
                        contentModifier
                    )
                    is ConnectionsUiState.Results.NoResults -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
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
            ConnectionsFormTopAppBar(
                title = title,
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            if(uiState is ConnectionsUiState.Form.HasData) {
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
    ) {
        LoadingContent(
            empty = when (uiState) {
                is ConnectionsUiState.Form.HasData -> false
                is ConnectionsUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is ConnectionsUiState.Form.HasData -> {

                        hasDataContent(uiState, modifier)

                    }
                    is ConnectionsUiState.Form.NoData -> {
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
fun ConnectionsForm(
    modifier: Modifier = Modifier,
    options: List<Stop>,
    formData: ConnectionsFormData,
    onFormDataUpdate: (ConnectionsFormData) -> Unit
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            ConnectionsStopLocation(
                selectedStop = formData.selectedStopFrom,
                onSelectedTrackChange = { onFormDataUpdate(formData.copy(selectedStopFrom = it)) },
                options
            )
            TimeSelector(time = formData.selectedTimeFrom, onTimeSelected = { onFormDataUpdate(formData.copy(selectedTimeFrom = it)) })
            Spacer(Modifier.size(32.dp))
            ConnectionsStopLocation(
                selectedStop = formData.selectedStopTo,
                onSelectedTrackChange = { onFormDataUpdate(formData.copy(selectedStopTo = it)) },
                options
            )
            TimeSelector(time = formData.selectedTimeTo, onTimeSelected = { onFormDataUpdate(formData.copy(selectedTimeTo = it)) })
        }
    }
}

/*
@Preview("ConnectionsFormPreview")
@Composable
fun ConnectionsFormPreview() {
    val options = listOf(
        Stop(1, "Albert", true),
        Stop(2, "Englišova", true)
    )

    TrefuTheme(false) {
        ConnectionsForm(
            options = options,
            selectedStop = options[0],
            onSelectedTrackChange = { }
        )
    }
}
*/

@Composable
fun ConnectionsList(
    modifier: Modifier = Modifier,
    connections: List<Connection>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items = connections) { _, item ->
            ConnectionItem(connection = item)
        }
    }
}

@Composable
fun ConnectionItem(
    connection: Connection
) {
    Column {
        connection.connectionsParts.forEach {
            ConnectionSubItem(
                it.to, it.from, it.lineShortCode
            )
        }
    }
}

@Composable
fun ConnectionSubItem(
    to: DepartureSimple,
    from: DepartureSimple,
    lineShortCode: String
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
                text = lineShortCode,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = formatter.format(from.time),
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = from.stopName,
                )
            }
            Row {
                Text(
                    text = formatter.format(to.time),
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = to.stopName,
                )
            }
        }
    }
}

@Preview("ConnectionItem Preview")
@Composable
fun ConnectionItemPreview() {
    TrefuTheme {
        ConnectionItem(connection =
            Connection(
                listOf(
                    ConnectionPart(
                        lineShortCode = "208",
                        from = DepartureSimple(
                            LocalTime.of(10, 20),
                            "start"
                        ),
                        to = DepartureSimple(
                            LocalTime.of(10, 28),
                            "end"
                        )
                    ),
                    ConnectionPart(
                        lineShortCode = "219",
                        from = DepartureSimple(
                            LocalTime.of(10, 29),
                            "start"
                        ),
                        to = DepartureSimple(
                            LocalTime.of(10, 41),
                            "end"
                        )
                    )
                )
            )
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConnectionsStopLocation(
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
fun ConnectionsFormTopAppBar(
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
fun ConnectionsResultsTopAppBar(
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
                    contentDescription = stringResource(R.string.cd_close_connections_results),
                )
            }
        },
        elevation = 0.dp
    )
}
