package cz.davidkurzica.trefu.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.presentation.components.appbar.SearchAppBar

@Composable
fun <T> FullScreenSelection(
    options: List<T>,
    filterBy: (T) -> String,
    onSelectedChange: (T) -> Unit,
    selectedOption: T,
    scaffoldState: ScaffoldState,
    onCloseSelection: () -> Unit,
    displayValue: (T) -> String,
) {
    var filter by remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SearchAppBar(
                text = filter,
                onTextChange = { filter = it },
                onCloseClicked = onCloseSelection,
                onSearchClicked = {},
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(
                options.filter { filterBy(it).lowercase().startsWith(filter.lowercase()) }
            ) {
                FullScreenSelectionItem(
                    selected = it == selectedOption,
                    item = it,
                    onSelect = { selected ->
                        onSelectedChange(selected)
                        onCloseSelection()
                    },
                    text = displayValue(it),
                )
            }
        }
    }

    BackHandler {
        onCloseSelection()
    }
}

@Composable
fun <T> FullScreenSelectionItem(
    selected: Boolean,
    item: T,
    onSelect: (T) -> Unit,
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = { onSelect(item) }
            )
            .padding(12.dp)
            .fillMaxWidth()
    )
}