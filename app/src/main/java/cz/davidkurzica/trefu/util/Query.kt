package cz.davidkurzica.trefu.adapter

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloException
import cz.davidkurzica.trefu.data.Result

suspend fun <D : Query.Data> ApolloClient.queryResult(query: Query<D>): Result<D> {
    return try {
        Result.Success(this.query(query).execute().dataAssertNoErrors)
    } catch (exception: ApolloException) {
        Result.Error(exception)
    }
}