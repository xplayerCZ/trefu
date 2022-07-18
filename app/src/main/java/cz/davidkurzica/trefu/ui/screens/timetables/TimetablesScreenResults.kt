package cz.davidkurzica.trefu.ui.screens.timetables

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
import cz.davidkurzica.trefu.ui.components.appbar.TrefuReturnTopAppBar
import cz.davidkurzica.trefu.ui.components.list.TimetablesList

@Composable
fun ResultsScreen(
    uiState: TimetablesUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {
    TimetablesScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        closeResults = closeResults,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasTimetablesUiState, contentModifier ->
        TimetablesList(
            timetable = hasTimetablesUiState.timetables,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun TimetablesScreenWithList(
    uiState: TimetablesUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasTimetablesContent: @Composable (
        uiState: TimetablesUiState.Results.HasResults,
        modifier: Modifier,
    ) -> Unit,
) {
    val title = stringResource(id = R.string.timetables_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            TrefuReturnTopAppBar(
                title = title,
                iconDescription = stringResource(id = R.string.cd_close_timetables_results),
                onIconClick = closeResults,
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is TimetablesUiState.Results.HasResults -> false
                is TimetablesUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = { },
            content = {
                when (uiState) {
                    is TimetablesUiState.Results.HasResults -> hasTimetablesContent(
                        uiState,
                        contentModifier
                    )
                    is TimetablesUiState.Results.NoResults -> {
                        Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                    }
                }
            }
        )
    }
}