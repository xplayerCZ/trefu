package cz.davidkurzica.trefu.ui.components.selector

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop

@Composable
fun FocusableStopSelector(
    selectedStop: Stop,
    onClick: () -> Unit,
) {
    FocusableSelector(
        label = "Stop",
        value = selectedStop.name,
        onClick = onClick,
    )
}

@Composable
fun FocusableLineSelector(
    selectedLine: Line,
    onClick: () -> Unit,
) {
    FocusableSelector(
        label = "Line",
        value = selectedLine.shortCode,
        onClick = onClick,
    )
}

@Composable
fun FocusableSelector(
    value: String,
    label: String,
    onClick: () -> Unit,
) {
    TextField(
        value = value,
        onValueChange = { },
        modifier = Modifier.clickable { onClick() },
        label = { Text(label) },
        enabled = false,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DirectionSelector(
    selectedDirection: Direction,
    onSelectedDirectionChange: (Direction) -> Unit,
    options: List<Direction>,
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