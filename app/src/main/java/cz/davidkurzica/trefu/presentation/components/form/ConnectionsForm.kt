package cz.davidkurzica.trefu.presentation.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.presentation.components.picker.TimeSelector
import cz.davidkurzica.trefu.presentation.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.presentation.screens.connections.ConnectionsFocusState
import java.time.LocalTime

@Composable
fun ConnectionsForm(
    modifier: Modifier = Modifier,
    selectedFromStop: StopOption,
    selectedToStop: StopOption,
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    onFocusChange: (ConnectionsFocusState) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            FocusableStopSelector(
                selectedStop = selectedFromStop,
                onClick = { onFocusChange(ConnectionsFocusState.StopFrom) },
            )
            Spacer(Modifier.size(16.dp))
            FocusableStopSelector(
                selectedStop = selectedToStop,
                onClick = { onFocusChange(ConnectionsFocusState.StopTo) },
            )
            Spacer(Modifier.size(16.dp))
            TimeSelector(
                time = selectedTime,
                onTimeSelected = onTimeChange
            )
        }
    }
}