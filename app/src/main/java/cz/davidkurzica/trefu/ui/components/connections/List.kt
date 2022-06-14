package cz.davidkurzica.trefu.ui.components.connections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.davidkurzica.trefu.model.Connection
import cz.davidkurzica.trefu.model.ConnectionPart
import cz.davidkurzica.trefu.model.DepartureSimple
import cz.davidkurzica.trefu.ui.theme.TrefuTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ConnectionsList(
    modifier: Modifier = Modifier,
    connections: List<Connection>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(items = connections) { _, item ->
            ConnectionItem(connection = item)
        }
    }
}

@Composable
fun ConnectionItem(
    connection: Connection
) {
    Card(
        elevation = 4.dp
    ) {
        Column {
            connection.connectionsParts.forEach {
                ConnectionSubItem(
                    it.to, it.from, it.lineShortCode
                )
            }
        }
    }
}

@Composable
fun ConnectionSubItem(
    to: DepartureSimple,
    from: DepartureSimple,
    lineShortCode: String
) {

    Column(
        modifier = androidx.compose.ui.Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        Text(
            text = lineShortCode,
            fontWeight = FontWeight.Bold
        )
        Row {
            Text(
                text = formatter.format(from.time),
            )
            Spacer(androidx.compose.ui.Modifier.size(16.dp))
            Text(
                text = from.stopName,
            )
        }
        Row {
            Text(
                text = formatter.format(to.time),
            )
            Spacer(androidx.compose.ui.Modifier.size(16.dp))
            Text(
                text = to.stopName,
            )
        }

    }
}

@Preview("ConnectionItem Preview")
@Composable
fun ConnectionItemPreview() {
    TrefuTheme {
        ConnectionItem(
            connection =
            Connection(
                listOf(
                    ConnectionPart(
                        lineShortCode = "208",
                        from = DepartureSimple(
                            LocalTime.of(10, 20),
                            "start"
                        ),
                        to = DepartureSimple(
                            LocalTime.of(10, 28),
                            "end"
                        )
                    ),
                    ConnectionPart(
                        lineShortCode = "219",
                        from = DepartureSimple(
                            LocalTime.of(10, 29),
                            "start"
                        ),
                        to = DepartureSimple(
                            LocalTime.of(10, 41),
                            "end"
                        )
                    )
                )
            )
        )
    }
}