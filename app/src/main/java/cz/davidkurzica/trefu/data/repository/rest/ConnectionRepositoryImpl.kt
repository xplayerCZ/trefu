package cz.davidkurzica.trefu.data.repository.rest

import cz.davidkurzica.trefu.domain.Connection
import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.DepartureSimple
import cz.davidkurzica.trefu.domain.repository.ConnectionRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient
import java.time.LocalTime

class ConnectionRepositoryImpl(
    private val httpClient: HttpClient,
) : ConnectionRepository {

    override suspend fun getConnectionSets(
        fromStopId: Int,
        toStopId: Int,
        after: LocalTime,
    ): Result<List<ConnectionSet>> {

        return Result.Success(
            listOf(
                ConnectionSet(
                    connections = listOf(
                        Connection(
                            lineShortCode = "1",
                            from = DepartureSimple(
                                time = LocalTime.of(8, 0),
                                stopName = "Stop 1",
                            ),
                            to = DepartureSimple(
                                time = LocalTime.of(8, 30),
                                stopName = "Stop 2",
                            ),
                        ),
                        Connection(
                            lineShortCode = "2",
                            from = DepartureSimple(
                                time = LocalTime.of(8, 35),
                                stopName = "Stop 2",
                            ),
                            to = DepartureSimple(
                                time = LocalTime.of(9, 0),
                                stopName = "Stop 3",
                            ),
                        ),
                    )
                ),
                ConnectionSet(
                    connections = listOf(
                        Connection(
                            lineShortCode = "3",
                            from = DepartureSimple(
                                time = LocalTime.of(8, 10),
                                stopName = "Stop 1",
                            ),
                            to = DepartureSimple(
                                time = LocalTime.of(8, 40),
                                stopName = "Stop 2",
                            ),
                        ),
                        Connection(
                            lineShortCode = "4",
                            from = DepartureSimple(
                                time = LocalTime.of(8, 45),
                                stopName = "Stop 2",
                            ),
                            to = DepartureSimple(
                                time = LocalTime.of(9, 10),
                                stopName = "Stop 3",
                            ),
                        ),
                    )
                ),
            )
        )
    }
}
