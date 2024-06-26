package cz.davidkurzica.trefu.presentation.screens.departures

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.presentation.components.EmptyResult
import cz.davidkurzica.trefu.presentation.components.FullScreenLoading
import cz.davidkurzica.trefu.presentation.components.LoadingContent
import cz.davidkurzica.trefu.presentation.components.TrefuSnackbarHost
import cz.davidkurzica.trefu.presentation.components.appbar.TrefuReturnTopAppBar
import cz.davidkurzica.trefu.presentation.components.list.DeparturesList


@Composable
fun ResultsScreen(
    uiState: DeparturesUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
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
        modifier: Modifier,
    ) -> Unit,
) {
    val title = stringResource(id = R.string.departures_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            TrefuReturnTopAppBar(
                title = title,
                iconDescription = stringResource(R.string.cd_close_departures_results),
                onIconClick = closeResults
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
            loading = uiState.isLoading,
            onRefresh = { },
            content = {
                when (uiState) {
                    is DeparturesUiState.Results.HasResults -> hasDeparturesContent(
                        uiState,
                        contentModifier
                    )
                    is DeparturesUiState.Results.NoResults -> {
                        EmptyResult(contentModifier.fillMaxSize())
                    }
                }
            }
        )
    }
}


