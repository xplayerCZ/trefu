package cz.davidkurzica.trefu.ui.components.timetables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.TimetablesFormData
import cz.davidkurzica.trefu.ui.screens.timetables.FocusStates
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

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
    onFocusChange: (FocusStates) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TimetablesStopSelector(
                selectedStop = formData.selectedStop,
                onClick = { onFocusChange(FocusStates.Stop) },
            )
            Spacer(Modifier.size(16.dp))
            TimetablesLineSelector(
                selectedLine = formData.selectedLine,
                onClick = { onFocusChange(FocusStates.Line) },
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

@Composable
fun TimetablesStopSelector(
    selectedStop: Stop,
    onClick: () -> Unit,
) {
    TextField(
        value = selectedStop.name,
        onValueChange = { },
        modifier = Modifier.clickable { onClick() },
        label = { Text("Stop") },
        enabled = false,
    )
}

@Composable
fun TimetablesLineSelector(
    selectedLine: Line,
    onClick: () -> Unit,
) {
    TextField(
        value = selectedLine.shortCode,
        onValueChange = { },
        modifier = Modifier.clickable { onClick() },
        label = { Text("Line") },
        enabled = false,
    )
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

@Preview(showBackground = true)
@Composable
fun TimetablesFormPreview() {
    TrefuTheme {
        TimetablesForm(
            lines = listOf(),
            directions = listOf(),
            stops = listOf(),
            formData = TimetablesFormData(
                selectedStop = Stop(
                    id = -1,
                    name = "",
                    enabled = true
                ),
                selectedLine = Line(
                    id = -1,
                    shortCode = ""
                ),
                selectedDirection = Direction(
                    id = -1,
                    description = "",
                )
            ),
            onDirectionChange = {},
            onLineChange = {},
            onStopChange = {},
            onFocusChange = {},
        )
    }
}