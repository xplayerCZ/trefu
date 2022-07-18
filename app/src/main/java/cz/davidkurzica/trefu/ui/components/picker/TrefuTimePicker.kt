package cz.davidkurzica.trefu.ui.components.picker

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimeSelector(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
) {
    val openDialog = remember { mutableStateOf(false) }

    TextButton(
        onClick = { openDialog.value = true }
    ) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        Text(
            text = "Time: ${formatter.format(time)}",
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.onPrimary
        )
    }

    if (openDialog.value) {
        TrefuTimePickerDialog(
            time = time,
            onTimeSelected = onTimeSelected,
            onDismissRequest = { openDialog.value = false }
        )
    }
}