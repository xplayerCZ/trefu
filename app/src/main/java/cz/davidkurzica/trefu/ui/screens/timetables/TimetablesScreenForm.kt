package cz.davidkurzica.trefu.ui.screens.timetables

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
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.TimetablesFormData
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.timetables.TimetablesForm
import cz.davidkurzica.trefu.ui.components.timetables.TimetablesFormTopAppBar

@Composable
fun FormScreen(
    uiState: TimetablesUiState.Form,
    onFormSubmit: (TimetablesFormData) -> Unit,
    onFormClean: () -> Unit,
    onDirectionUpdate: (Direction) -> Unit,
    onLineUpdate: (Line) -> Unit,
    onStopUpdate: (Stop) -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    TimetablesScreenWithForm(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        scaffoldState = scaffoldState,
        onFormSubmit = onFormSubmit,
        modifier = modifier
    ) { hasDataUiState, contentModifier ->
        TimetablesForm(
            modifier = contentModifier,
            formData = hasDataUiState.formData,
            onLineChange = onLineUpdate,
            onStopChange = onStopUpdate,
            onDirectionChange = onDirectionUpdate,
            stops = hasDataUiState.stops,
            lines = hasDataUiState.lines,
            directions = hasDataUiState.directions
        )
    }
}


@Composable
private fun TimetablesScreenWithForm(
    uiState: TimetablesUiState.Form,
    onErrorDismiss: (Long) -> Unit,
    onFormSubmit: (TimetablesFormData) -> Unit,
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDataContent: @Composable (
        uiState: TimetablesUiState.Form.HasData,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.timetables_title)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TimetablesFormTopAppBar(
                title = title,
                openDrawer = openDrawer
            )
        },
        floatingActionButton = {
            if (uiState is TimetablesUiState.Form.HasData) {
                FloatingActionButton(
                    onClick = {
                        onFormSubmit(uiState.formData)
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search for timetables"
                    )
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LoadingContent(
            empty = when (uiState) {
                is TimetablesUiState.Form.HasData -> false
                is TimetablesUiState.Form.NoData -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is TimetablesUiState.Form.HasData -> hasDataContent(
                        uiState,
                        modifier.padding(padding)
                    )
                    is TimetablesUiState.Form.NoData -> Box(modifier.fillMaxSize()) { /* empty screen */ Text(
                        uiState.isLoading.toString()
                    )
                    }
                }
            }
        )
    }
}
