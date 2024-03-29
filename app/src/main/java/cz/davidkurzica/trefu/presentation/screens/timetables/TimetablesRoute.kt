package cz.davidkurzica.trefu.presentation.screens.timetables

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.RouteDirection
import cz.davidkurzica.trefu.domain.StopOption

@Composable
fun TimetablesRoute(
    timetablesViewModel: TimetablesViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    val uiState by timetablesViewModel.uiState.collectAsState()

    TimetablesRoute(
        uiState = uiState,
        onFormSubmit = { stopId, routeId, lineShortCode ->
            timetablesViewModel.submitForm(
                stopId,
                routeId,
                lineShortCode
            )
        },
        onLineChange = { timetablesViewModel.updateLine(it) },
        onStopChange = { timetablesViewModel.updateStop(it) },
        onDirectionChange = { timetablesViewModel.updateDirection(it) },
        onFormRefresh = { timetablesViewModel.refreshForm() },
        onErrorDismiss = { timetablesViewModel.errorShown(it) },
        closeResults = { timetablesViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun TimetablesRoute(
    uiState: TimetablesUiState,
    onFormSubmit: (Int, Int, String) -> Unit,
    onFormRefresh: () -> Unit,
    onLineChange: (Line) -> Unit,
    onStopChange: (StopOption) -> Unit,
    onDirectionChange: (RouteDirection) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
) {
    when (getTimetablesScreenType(uiState)) {
        TimetablesScreenType.Form -> {
            FormScreen(
                uiState = uiState as TimetablesUiState.Form,
                onFormSubmit = onFormSubmit,
                onFormRefresh = onFormRefresh,
                onLineChange = onLineChange,
                onStopChange = onStopChange,
                onDirectionChange = onDirectionChange,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
            )
        }
        TimetablesScreenType.Results -> {
            ResultsScreen(
                uiState = uiState as TimetablesUiState.Results,
                onErrorDismiss = onErrorDismiss,
                closeResults = closeResults,
                scaffoldState = scaffoldState,
            )

            BackHandler {
                closeResults()
            }
        }
    }
}

private enum class TimetablesScreenType {
    Form,
    Results,
}

private fun getTimetablesScreenType(
    uiState: TimetablesUiState,
): TimetablesScreenType = when (uiState.isResultsOpen) {
    false -> TimetablesScreenType.Form
    true -> TimetablesScreenType.Results
}