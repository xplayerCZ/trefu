package cz.davidkurzica.trefu.ui.departures

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.ui.components.Time
import cz.davidkurzica.trefu.ui.components.TrefuTimePickerDialog
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

@Composable
fun DeparturesScreen(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val title = stringResource(id = R.string.departures_title)
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search for departures")
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        DeparturesScreenContent(screenModifier)
    }
}

@Composable
fun DeparturesScreenContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        DeparturesForm()
    }
}

@Composable
fun DeparturesForm() {
    Column {
        DeparturesStopLocation()
        DeparturesTime()
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
fun DeparturesStopLocation() {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
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
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Preview("Departures Screen")
@Composable
fun DeparturesScreenPreview() {
    TrefuTheme {
        Surface {
            DeparturesScreen(openDrawer = { }, scaffoldState = rememberScaffoldState())
        }
    }
}