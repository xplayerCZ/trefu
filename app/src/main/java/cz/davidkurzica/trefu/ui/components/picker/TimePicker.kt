package cz.davidkurzica.trefu.ui.components.picker

import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalTime

@Composable
fun TrefuTimePickerDialog(
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select time".uppercase(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onPrimary
                )
            }

            TimePickerDialog(time = time, onTimeChanged = onTimeSelected)

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )
                }

                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.button,
                        color = MaterialTheme.colors.primary
                    )
                }

            }
        }
    }
}

@Composable
fun TimePickerDialog(time: LocalTime, onTimeChanged: (LocalTime) -> Unit) {
    // Adds view to Compose
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            TimePicker(context)
            //TimePicker(ContextThemeWrapper(context, R.style.TimePicker))
        },
        update = { view ->
            run {
                view.hour = time.hour
                view.minute = time.minute
                view.setIs24HourView(true)
                view.setOnTimeChangedListener { _, hours, minutes ->
                    onTimeChanged(
                        LocalTime.of(
                            hours,
                            minutes
                        )
                    )
                }
            }
        }
    )
}

@Preview("Custom Time Picker")
@Composable
fun TimePickerPreview() {
    TrefuTheme {
        Surface {
            TrefuTimePickerDialog(
                onTimeSelected = { },
                onDismissRequest = {},
                time = LocalTime.of(12, 24)
            )
        }
    }
}