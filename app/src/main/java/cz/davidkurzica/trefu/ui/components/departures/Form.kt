package cz.davidkurzica.trefu.ui.components.departures

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
            FocusableStopSelector(
                selectedStop = selectedStop,
                onClick = { },
            )
            TimeSelector(
                time = selectedTime,
                onTimeSelected = onSelectedTimeChange
            )
        }
    }
}