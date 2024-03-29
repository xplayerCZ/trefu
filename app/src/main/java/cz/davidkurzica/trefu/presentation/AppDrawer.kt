package cz.davidkurzica.trefu.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.presentation.components.NavigationIcon
import cz.davidkurzica.trefu.presentation.ui.theme.TrefuTheme

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToHome: () -> Unit,
    navigateToDepartures: () -> Unit,
    navigateToConnections: () -> Unit,
    navigateToTimetables: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .2f))
        DrawerButton(
            icon = Icons.Filled.Home,
            label = stringResource(id = R.string.home_title),
            isSelected = currentRoute == TrefuDestinations.HOME_ROUTE,
            action = {
                navigateToHome()
                closeDrawer()
            }
        )

        DrawerButton(
            icon = Icons.Filled.DepartureBoard,
            label = stringResource(id = R.string.departures_title),
            isSelected = currentRoute == TrefuDestinations.DEPARTURES_ROUTE,
            action = {
                navigateToDepartures()
                closeDrawer()
            }
        )

        DrawerButton(
            icon = Icons.Filled.AltRoute,
            label = stringResource(id = R.string.connections_title),
            isSelected = currentRoute == TrefuDestinations.CONNECTIONS_ROUTE,
            action = {
                navigateToConnections()
                closeDrawer()
            }
        )

        DrawerButton(
            icon = Icons.Filled.Assignment,
            label = stringResource(id = R.string.timetables_title),
            isSelected = currentRoute == TrefuDestinations.TIMETABLES_ROUTE,
            action = {
                navigateToTimetables()
                closeDrawer()
            }
        )
    }
}

@Composable
private fun DrawerButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    action: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colors
    val textIconColor = if (isSelected) {
        colors.onPrimary
    } else {
        colors.onSurface.copy(alpha = 0.6f)
    }
    val backgroundColor = if (isSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    val surfaceModifier = modifier
        .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        .fillMaxWidth()
    Surface(
        modifier = surfaceModifier,
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(
            onClick = action,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationIcon(
                    icon = icon,
                    isSelected = isSelected,
                    contentDescription = null, // decorative
                    tintColor = textIconColor
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2,
                    color = textIconColor
                )
            }
        }
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    TrefuTheme {
        Surface {
            AppDrawer(
                currentRoute = TrefuDestinations.HOME_ROUTE,
                navigateToHome = {},
                navigateToDepartures = {},
                navigateToConnections = {},
                navigateToTimetables = {},
                closeDrawer = {}
            )
        }
    }
}
