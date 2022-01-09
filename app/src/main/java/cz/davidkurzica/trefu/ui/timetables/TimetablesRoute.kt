package cz.davidkurzica.trefu.ui.timetables

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.model.Stop
import java.time.LocalTime

@Composable
fun TimetablesRoute(
    timetablesViewModel: TimetablesViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by timetablesViewModel.uiState.collectAsState()

    TimetablesRoute(
        uiState = uiState,
        onFormSubmit = { stopId, timeId -> timetablesViewModel.submitForm(stopId, timeId) },
        onFormUpdate = { timetablesViewModel.updateForm(it) },
        onFormClean = { timetablesViewModel.cleanForm() },
        onErrorDismiss = { timetablesViewModel.errorShown(it) },
        closeResults = { timetablesViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun TimetablesRoute(
    uiState: TimetablesUiState,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (Stop) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val timetablesScreenType = getTimetablesScreenType(uiState)
    when (timetablesScreenType) {
        TimetablesScreenType.Form -> {
            FormScreen(
                uiState = uiState as TimetablesUiState.Form,
                onFormSubmit = onFormSubmit,
                onFormClean = onFormClean,
                onFormUpdate = onFormUpdate,
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

@Composable
private fun getTimetablesScreenType(
    uiState: TimetablesUiState
): TimetablesScreenType = when (uiState.isResultsOpen) {
    false -> TimetablesScreenType.Form
    true -> TimetablesScreenType.Results
}