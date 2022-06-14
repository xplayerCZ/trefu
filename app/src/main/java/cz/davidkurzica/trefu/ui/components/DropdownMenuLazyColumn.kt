package cz.davidkurzica.trefu.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T>DropdownMenuLazyColumn(
    items: List<T>,
    content: @Composable (Int, T) -> Unit
) {
    LazyColumn {
        itemsIndexed(items = items) { index, item ->
            DropdownMenuItemContainer {
                content(index, item)
            }
        }
    }
}

@Composable
fun DropdownMenuItemContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

}
