package cz.davidkurzica.trefu.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.ui.theme.TrefuTheme

@Composable
fun TrefuIcon(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null, // decorative
        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
        modifier = modifier
    )
}

@Composable
fun NavigationIcon(
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tintColor: Color? = null,
) {
    val imageAlpha = if (isSelected) {
        1f
    } else {
        0.6f
    }

    val iconTintColor = tintColor ?: if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
    }

    Image(
        modifier = modifier,
        imageVector = icon,
        contentDescription = contentDescription,
        contentScale = ContentScale.Inside,
        colorFilter = ColorFilter.tint(iconTintColor),
        alpha = imageAlpha
    )
}

@Preview("Trefu icon")
@Preview("Trefu icon (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTrefuIcon() {
    TrefuTheme {
        Surface {
            TrefuIcon()
        }
    }
}

@Preview("Navigation icon")
@Preview("Navigation (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewNavigationIcon() {
    TrefuTheme {
        Surface {
            NavigationIcon(icon = Icons.Filled.Home, isSelected = true)
        }
    }
}
