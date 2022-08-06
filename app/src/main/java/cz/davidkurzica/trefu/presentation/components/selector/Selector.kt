package cz.davidkurzica.trefu.presentation.components.selector

import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.RouteDirection
import cz.davidkurzica.trefu.domain.StopOption

@Composable
fun FocusableStopSelector(
    selectedStop: StopOption,
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
    selectedDirection: RouteDirection,
    onSelectedDirectionChange: (RouteDirection) -> Unit,
    options: List<RouteDirection>,
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