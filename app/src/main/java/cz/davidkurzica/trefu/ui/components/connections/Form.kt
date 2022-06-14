package cz.davidkurzica.trefu.ui.components.connections

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.ConnectionsFormData
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.TimeSelector

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
            TimeSelector(
                time = formData.selectedTimeFrom,
                onTimeSelected = { onFormDataUpdate(formData.copy(selectedTimeFrom = it)) })
            Spacer(Modifier.size(32.dp))
            ConnectionsStopLocation(
                selectedStop = formData.selectedStopTo,
                onSelectedTrackChange = { onFormDataUpdate(formData.copy(selectedStopTo = it)) },
                options
            )
            TimeSelector(
                time = formData.selectedTimeTo,
                onTimeSelected = { onFormDataUpdate(formData.copy(selectedTimeTo = it)) })
        }
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