package cz.davidkurzica.trefu.presentation.components.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.presentation.components.picker.TimeSelector
import cz.davidkurzica.trefu.presentation.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.presentation.screens.departures.DeparturesFocusState
import java.time.LocalTime

@Composable
fun DeparturesForm(
    modifier: Modifier = Modifier,
    selectedStop: StopOption,
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