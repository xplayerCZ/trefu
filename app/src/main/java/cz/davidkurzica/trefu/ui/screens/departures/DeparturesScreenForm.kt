package cz.davidkurzica.trefu.ui.screens.departures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.TrefuDefaultTopAppBar
import cz.davidkurzica.trefu.ui.components.departures.DeparturesForm
import java.time.LocalTime

@Composable
fun FormScreen(
    uiState: DeparturesUiState.Form,
    onFormSubmit: (Int, LocalTime) -> Unit,
    onFormClean: () -> Unit,
    onSelectedTrackUpdate: (Stop) -> Unit,
    onSelectedTimeUpdate: (LocalTime) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    DeparturesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->
        DeparturesForm(
            modifier = contentModifier,
            selectedStop = hasDataUiState.selectedStop,
            options = hasDataUiState.stops,
            onSelectedTrackChange = onSelectedTrackUpdate,
            selectedTime = hasDataUiState.selectedTime,
            onSelectedTimeChange = onSelectedTimeUpdate
        )
    }
}

@Composable
private fun DeparturesScreenWithForm(
    uiState: DeparturesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (Int, LocalTime) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: DeparturesUiState.Form.HasData,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.departures_title)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TrefuDefaultTopAppBar(
                title = title,
                iconDescription = stringResource(R.string.cd_open_navigation_drawer),
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            if (uiState is DeparturesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = {
                        onFormSubmit(
                            uiState.selectedStop.id,
                            uiState.selectedTime
                        )
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for departures"
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LoadingContent(
            empty = when (uiState) {
                is DeparturesUiState.Form.HasData -> false
                is DeparturesUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is DeparturesUiState.Form.HasData -> hasDataContent(
                        uiState,
                        modifier.padding(padding)
                    )
                    is DeparturesUiState.Form.NoData -> Box(modifier.fillMaxSize()) { /* empty screen */ }
                }
            }
        )
    }
}