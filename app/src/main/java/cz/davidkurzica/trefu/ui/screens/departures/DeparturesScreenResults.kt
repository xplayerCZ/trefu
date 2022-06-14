package cz.davidkurzica.trefu.ui.screens.departures

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.ui.components.FullScreenLoading
import cz.davidkurzica.trefu.ui.components.LoadingContent
import cz.davidkurzica.trefu.ui.components.TrefuSnackbarHost
import cz.davidkurzica.trefu.ui.components.departures.DeparturesList
import cz.davidkurzica.trefu.ui.components.departures.DeparturesResultsTopAppBar


@Composable
fun ResultsScreen(
    uiState: DeparturesUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    DeparturesScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        closeResults = closeResults,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasDeparturesUiState, contentModifier ->
        DeparturesList(
            departureWithLines = hasDeparturesUiState.departureWithLines,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun DeparturesScreenWithList(
    uiState: DeparturesUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasDeparturesContent: @Composable (
        uiState: DeparturesUiState.Results.HasResults,
        modifier: Modifier
    ) -> Unit
) {
    val title = stringResource(id = R.string.departures_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            DeparturesResultsTopAppBar(
                title = title,
                closeResults = closeResults,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is DeparturesUiState.Results.HasResults -> false
                is DeparturesUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            content = {
                when (uiState) {
                    is DeparturesUiState.Results.HasResults -> hasDeparturesContent(
                        uiState,
                        contentModifier
                    )
                    is DeparturesUiState.Results.NoResults -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}


