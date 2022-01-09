package cz.davidkurzica.trefu.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun DeparturesTime() {
    val openDialog = remember { mutableStateOf(false) }
    val (time, setTime) = remember { mutableStateOf(Time(12, 0)) }

    TextButton(
        onClick = { openDialog.value = true }
    ) {
        val text = "%02d %02d".format(time.hours, time.minutes)
        Text(
            text = text,
            style = MaterialTheme.typography.button,
            color = MaterialTheme.colors.primary
        )
    }

    if (openDialog.value) {
        TrefuTimePickerDialog(
            time = time,
            onTimeSelected = setTime,
            onDismissRequest = { openDialog.value = false }
        )
    }
}