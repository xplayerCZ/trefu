package cz.davidkurzica.trefu.presentation.screens.connections

import androidx.compose.foundation.layout.Box
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
import cz.davidkurzica.trefu.presentation.components.list.ConnectionsList

@Composable
fun ResultsScreen(
    uiState: ConnectionsUiState.Results,
    closeResults: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
) {
    ConnectionsScreenWithList(
        uiState = uiState,
        onErrorDismiss = onErrorDismiss,
        closeResults = closeResults,
        scaffoldState = scaffoldState,
        modifier = modifier
    ) { hasConnectionsUiState, contentModifier ->
        ConnectionsList(
            connectionSets = hasConnectionsUiState.connectionSets,
            modifier = contentModifier,
        )
    }

}

@Composable
private fun ConnectionsScreenWithList(
    uiState: ConnectionsUiState.Results,
    onErrorDismiss: (Long) -> Unit,
    closeResults: () -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasConnectionsContent: @Composable (
        uiState: ConnectionsUiState.Results.HasResults,
        modifier: Modifier,
    ) -> Unit,
) {
    val title = stringResource(id = R.string.connections_title)
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { TrefuSnackbarHost(hostState = it) },
        topBar = {
            TrefuReturnTopAppBar(
                title = title,
                iconDescription = stringResource(R.string.cd_close_connections_results),
                onIconClick = closeResults
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is ConnectionsUiState.Results.HasResults -> false
                is ConnectionsUiState.Results.NoResults -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = { },
            content = {
                when (uiState) {
                    is ConnectionsUiState.Results.HasResults -> hasConnectionsContent(
                        uiState,
                        contentModifier
                    )
                    is ConnectionsUiState.Results.NoResults -> {
                        EmptyResult(contentModifier.fillMaxSize())
                    }
                }
            }
        )
    }
}
