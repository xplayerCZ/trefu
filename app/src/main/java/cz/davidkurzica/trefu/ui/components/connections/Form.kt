package cz.davidkurzica.trefu.ui.components.connections

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.ConnectionsFormData
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.TimeSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableStopSelector

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
            FocusableStopSelector(
                selectedStop = formData.selectedStopFrom,
                onClick = { },
            )
            Spacer(Modifier.size(32.dp))
            FocusableStopSelector(
                selectedStop = formData.selectedStopTo,
                onClick = { },
            )
            TimeSelector(
                time = formData.selectedTimeTo,
                onTimeSelected = { onFormDataUpdate(formData.copy(selectedTimeTo = it)) })
        }
    }
}