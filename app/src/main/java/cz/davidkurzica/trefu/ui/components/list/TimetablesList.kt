package cz.davidkurzica.trefu.ui.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.DepartureSimple
import cz.davidkurzica.trefu.model.Timetable
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TimetablesList(
    modifier: Modifier = Modifier,
    timetable: Timetable,
) {
    Column {
        Row(
            modifier.padding(8.dp)
        ) {
            Spacer(Modifier.size(16.dp))
            Text(
                text = timetable.lineShortCode,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = timetable.date.toString(),
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(items = timetable.departures) { _, item ->
                TimetableItem(timetable = item)
            }
        }
    }
}

@Composable
fun TimetableItem(
    timetable: DepartureSimple,
) {
    Card(
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            Text(
                text = formatter.format(timetable.time),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "konečná: ${timetable.stopName}",
                fontSize = MaterialTheme.typography.caption.fontSize
            )
        }
    }
}

@Preview("TimetableItem Preview")
@Composable
fun TimetableItemPreview() {
    TrefuTheme {
        TimetableItem(
            DepartureSimple(LocalTime.now().plusMinutes(5), "end")
        )
    }
}