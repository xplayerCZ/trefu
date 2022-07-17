package cz.davidkurzica.trefu.ui.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.selector.DirectionSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableLineSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.ui.screens.timetables.TimetablesFocusState

@Composable
fun TimetablesForm(
    modifier: Modifier = Modifier,
    directions: List<Direction>,
    selectedStop: Stop,
    selectedLine: Line,
    selectedDirection: Direction,
    onDirectionChange: (Direction) -> Unit,
    onFocusChange: (TimetablesFocusState) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            FocusableStopSelector(
                selectedStop = selectedStop,
                onClick = { onFocusChange(TimetablesFocusState.Stop) },
            )
            Spacer(Modifier.size(16.dp))
            FocusableLineSelector(
                selectedLine = selectedLine,
                onClick = { onFocusChange(TimetablesFocusState.Line) },
            )
            Spacer(Modifier.size(16.dp))
            DirectionSelector(
                selectedDirection = selectedDirection,
                onSelectedDirectionChange = onDirectionChange,
                directions
            )
        }
    }
}