package cz.davidkurzica.trefu.data.repository.rest

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.repository.LineRepository
import cz.davidkurzica.trefu.domain.util.Result
import io.ktor.client.HttpClient

class LineRepositoryImpl(
    private val httpClient: HttpClient,
) : LineRepository {

    override suspend fun getLines(id: Int): Result<Line> {
        TODO("Not yet implemented")
    }
}