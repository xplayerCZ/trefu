package cz.davidkurzica.trefu.data.repository.graphql

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.repository.LineRepository
import cz.davidkurzica.trefu.domain.util.Result

class LineRepositoryImpl(
    private val apolloClient: ApolloClient,
) : LineRepository {

    override suspend fun getLines(id: Int): Result<Line> {
        TODO("Not yet implemented")
    }
}