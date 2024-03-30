package cz.davidkurzica.trefu.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (empty) {
        emptyContent()
    } else {
        Box(
            contentAlignment = Alignment.TopCenter,
        ) {
            PullRefreshIndicator(
                refreshing = loading,
                state = rememberPullRefreshState(
                    refreshing = loading,
                    onRefresh = onRefresh,
                ),
            )
            if(!loading) content()
        }
    }
}