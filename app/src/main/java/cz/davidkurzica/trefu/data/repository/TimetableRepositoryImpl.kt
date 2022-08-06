package cz.davidkurzica.trefu.data.repository

import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.domain.Timetable
import cz.davidkurzica.trefu.domain.repository.TimetableRepository
import cz.davidkurzica.trefu.domain.util.Result

class TimetableRepositoryImpl(
    private val apolloClient: ApolloClient,
) : TimetableRepository {
    override suspend fun getTimetable(): Result<List<Timetable>> {
        TODO("Not yet implemented")
    }
}