package cz.davidkurzica.trefu.ui.departures

import androidx.activity.compose.BackHandler
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cz.davidkurzica.trefu.model.Track
import org.joda.time.LocalTime

@Composable
fun DeparturesRoute(
    departuresViewModel: DeparturesViewModel,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by departuresViewModel.uiState.collectAsState()

    DeparturesRoute(
        uiState = uiState,
        onFormSubmit = { stopId, timeId -> departuresViewModel.submitForm(stopId, timeId) },
        onFormUpdate = { departuresViewModel.updateForm(it) },
        onFormClean = { departuresViewModel.cleanForm() },
        onErrorDismiss = { departuresViewModel.errorShown(it) },
        onCloseResults = { departuresViewModel.closeResults() },
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
    )
}

@Composable
fun DeparturesRoute(
    uiState: DeparturesUiState,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onFormUpdate: (Track) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onCloseResults: () -> Unit,
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
                onFormUpdate = onFormUpdate,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
            )
        }
        DeparturesScreenType.Results -> {
            ResultsScreen(
                uiState = uiState as DeparturesUiState.Results,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
            )

            BackHandler {
                onCloseResults()
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