package cz.davidkurzica.trefu.ui.components.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.TimeSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.ui.screens.departures.DeparturesFocusState
import java.time.LocalTime

@Composable
fun DeparturesForm(
    modifier: Modifier = Modifier,
    selectedStop: Stop,
    selectedTime: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    onFocusChange: (DeparturesFocusState) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(top = 12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column {
            FocusableStopSelector(
                selectedStop = selectedStop,
                onClick = { onFocusChange(DeparturesFocusState.Stop) },
            )
            TimeSelector(
                time = selectedTime,
                onTimeSelected = onTimeChange
            )
        }
    }
}