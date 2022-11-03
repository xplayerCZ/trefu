package cz.davidkurzica.trefu.presentation.components.list

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
import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.presentation.ui.theme.TrefuTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DeparturesList(
    modifier: Modifier = Modifier,
    departureWithLines: List<DepartureWithLine>,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items = departureWithLines) { _, item ->
            DepartureItem(departureWithLine = item)
        }
    }
}

@Composable
fun DepartureItem(
    departureWithLine: DepartureWithLine,
) {
    Card(
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row {
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                Text(
                    text = departureWithLine.lineShortCode,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(16.dp))
                Text(
                    text = formatter.format(departureWithLine.time),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "konečná: ${departureWithLine.stopName}",
                fontSize = MaterialTheme.typography.caption.fontSize
            )
        }
    }
}

@Preview("DepartureItem Preview")
@Composable
fun DepartureItemPreview() {
    TrefuTheme {
        DepartureItem(
            departureWithLine = DepartureWithLine(
                LocalTime.of(18, 30),
                "208",
                "Malé Hoštice"
            )
        )
    }
}