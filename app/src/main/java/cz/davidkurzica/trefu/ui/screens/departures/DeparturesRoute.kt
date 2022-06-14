package cz.davidkurzica.trefu.ui.screens.departures

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.model.Stop
import java.time.LocalTime

@Composable
fun DeparturesRoute(
    departuresViewModel: DeparturesViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by departuresViewModel.uiState.collectAsState()

    DeparturesRoute(
        uiState = uiState,
        onFormSubmit = { stopId, time -> departuresViewModel.submitForm(stopId, time) },
        onSelectedTrackUpdate = { departuresViewModel.updateTrack(it) },
        onSelectedTimeUpdate = { departuresViewModel.updateTime(it) },
        onFormClean = { departuresViewModel.cleanForm() },
        onErrorDismiss = { departuresViewModel.errorShown(it) },
        closeResults = { departuresViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun DeparturesRoute(
    uiState: DeparturesUiState,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onSelectedTrackUpdate: (Stop) -> Unit,
    onSelectedTimeUpdate: (LocalTime) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val departuresScreenType = getDeparturesScreenType(uiState)
    when (departuresScreenType) {
        DeparturesScreenType.Form -> {
            FormScreen(
                uiState = uiState as DeparturesUiState.Form,
                onFormSubmit = onFormSubmit,
                onFormClean = onFormClean,
                onSelectedTimeUpdate = onSelectedTimeUpdate,
                onSelectedTrackUpdate = onSelectedTrackUpdate,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
                onErrorDismiss = onErrorDismiss
            )
        }
        DeparturesScreenType.Results -> {
            ResultsScreen(
                uiState = uiState as DeparturesUiState.Results,
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

private enum class DeparturesScreenType {
    Form,
    Results,
}

@Composable
private fun getDeparturesScreenType(
    uiState: DeparturesUiState
): DeparturesScreenType = when (uiState.isResultsOpen) {
    false -> DeparturesScreenType.Form
    true -> DeparturesScreenType.Results
}