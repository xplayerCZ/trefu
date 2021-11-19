package cz.davidkurzica.trefu.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

@Composable
fun HomeScreen(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState
) {
    val title = stringResource(id = R.string.home_title)
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.cd_open_navigation_drawer),
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        HomeScreenContent(screenModifier)
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier
) {
    Surface(modifier, color = Color.White) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("This page will contain favorites and shortcuts!")
        }
    }
}


@Preview("Home Screen")
@Composable
fun HomeScreenPreview() {
    TrefuTheme {
        Surface {
            HomeScreen(openDrawer = { }, scaffoldState = rememberScaffoldState())
        }
    }
}