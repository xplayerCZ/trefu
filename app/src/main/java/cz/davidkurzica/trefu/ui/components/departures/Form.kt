package cz.davidkurzica.trefu.ui.components.departures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.TimeSelector
import java.time.LocalTime

@Composable
fun DeparturesForm(
    modifier: Modifier = Modifier,
    options: List<Stop>,
    selectedStop: Stop,
    onSelectedTrackChange: (Stop) -> Unit,
    selectedTime: LocalTime,
    onSelectedTimeChange: (LocalTime) -> Unit
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
            TimeSelector(
                time = selectedTime,
                onTimeSelected = onSelectedTimeChange
            )
        }
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