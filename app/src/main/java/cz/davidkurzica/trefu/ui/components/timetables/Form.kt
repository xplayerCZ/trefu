package cz.davidkurzica.trefu.ui.components.timetables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.TimetablesFormData

@Composable
fun TimetablesForm(
    modifier: Modifier = Modifier,
    lines: List<Line>,
    directions: List<Direction>,
    stops: List<Stop>,
    formData: TimetablesFormData,
    onDirectionChange: (Direction) -> Unit,
    onLineChange: (Line) -> Unit,
    onStopChange: (Stop) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimetablesStopSelector(
                selectedStop = formData.selectedStop,
                onSelectedTrackChange = onStopChange,
                stops
            )
            Spacer(Modifier.size(16.dp))
            TimetablesLineSelector(
                selectedLine = formData.selectedLine,
                onSelectedLineChange = onLineChange,
                lines
            )
            Spacer(Modifier.size(16.dp))
            TimetablesDirectionSelector(
                selectedDirection = formData.selectedDirection,
                onSelectedDirectionChange = onDirectionChange,
                directions
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetablesStopSelector(
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
                        if (item != selectedStop) {
                            onSelectedTrackChange(item)
                        }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetablesLineSelector(
    selectedLine: Line,
    onSelectedLineChange: (Line) -> Unit,
    options: List<Line>
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
            value = selectedLine.shortCode,
            onValueChange = { },
            label = { Text("Line") },
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
                        if (item != selectedLine) {
                            onSelectedLineChange(item)
                        }
                        expanded = false
                    }
                ) {
                    Text(text = item.shortCode)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimetablesDirectionSelector(
    selectedDirection: Direction,
    onSelectedDirectionChange: (Direction) -> Unit,
    options: List<Direction>
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
            value = selectedDirection.description,
            onValueChange = { },
            label = { Text("Direction") },
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
                        if (item != selectedDirection) {
                            onSelectedDirectionChange(item)
                        }
                        expanded = false
                    }
                ) {
                    Text(text = item.description)
                }
            }
        }
    }
}