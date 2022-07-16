package cz.davidkurzica.trefu.ui.components.timetables

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.TimetablesFormData
import cz.davidkurzica.trefu.ui.components.selector.DirectionSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableLineSelector
import cz.davidkurzica.trefu.ui.components.selector.FocusableStopSelector
import cz.davidkurzica.trefu.ui.screens.timetables.FocusStates
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

@Composable
fun TimetablesForm(
    modifier: Modifier = Modifier,
    directions: List<Direction>,
    formData: TimetablesFormData,
    onDirectionChange: (Direction) -> Unit,
    onFocusChange: (FocusStates) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FocusableStopSelector(
                selectedStop = formData.selectedStop,
                onClick = { onFocusChange(FocusStates.Stop) },
            )
            Spacer(Modifier.size(16.dp))
            FocusableLineSelector(
                selectedLine = formData.selectedLine,
                onClick = { onFocusChange(FocusStates.Line) },
            )
            Spacer(Modifier.size(16.dp))
            DirectionSelector(
                selectedDirection = formData.selectedDirection,
                onSelectedDirectionChange = onDirectionChange,
                directions
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimetablesFormPreview() {
    TrefuTheme {
        TimetablesForm(
            directions = listOf(),
            formData = TimetablesFormData(
                selectedStop = Stop(
                    id = -1,
                    name = "",
                    enabled = true
                ),
                selectedLine = Line(
                    id = -1,
                    shortCode = ""
                ),
                selectedDirection = Direction(
                    id = -1,
                    description = "",
                )
            ),
            onDirectionChange = {},
            onFocusChange = {},
        )
    }
}