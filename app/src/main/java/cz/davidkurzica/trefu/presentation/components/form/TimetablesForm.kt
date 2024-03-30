package cz.davidkurzica.trefu.presentation.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.RouteDirection
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.presentation.components.selector.DirectionSelector
import cz.davidkurzica.trefu.presentation.components.selector.FocusableLineSelector
import cz.davidkurzica.trefu.presentation.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.presentation.screens.timetables.TimetablesFocusState

@Composable
fun TimetablesForm(
    modifier: Modifier = Modifier,
    directions: List<RouteDirection>,
    selectedStop: StopOption,
    selectedLine: Line,
    selectedDirection: RouteDirection,
    onDirectionChange: (RouteDirection) -> Unit,
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
                options = directions
            )
        }
    }
}