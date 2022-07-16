package cz.davidkurzica.trefu.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> FullScreenSelection(
    options: List<T>,
    onSelectedChange: (T) -> Unit,
    selectedOption: T,
    scaffoldState: ScaffoldState,
    onCloseSelection: () -> Unit,
    displayValue: (T) -> String,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("Select stop")
                },
                navigationIcon = {
                    IconButton(onClick = onCloseSelection) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Close Selection",
                        )
                    }
                },
                elevation = 0.dp
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(options) {
                FullScreenSelectionRow(
                    selected = it == selectedOption,
                    item = it,
                    onSelect = onSelectedChange,
                    text = displayValue(it),
                )
            }
        }
    }
}

@Composable
fun <T> FullScreenSelectionRow(
    selected: Boolean,
    item: T,
    onSelect: (T) -> Unit,
    text: String,
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onSelect(item) }
            ))
}