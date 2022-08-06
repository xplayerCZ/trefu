package cz.davidkurzica.trefu.presentation.components.appbar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable

@Composable
fun TrefuDefaultTopAppBar(
    title: String,
    iconDescription: String,
    openDrawer: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = iconDescription,
                )
            }
        },
    )
}

@Composable
fun TrefuReturnTopAppBar(
    title: String,
    iconDescription: String,
    onIconClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = iconDescription,
                )
            }
        },
    )
}