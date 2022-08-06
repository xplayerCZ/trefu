package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.Timetable
import cz.davidkurzica.trefu.domain.util.Result

interface TimetableRepository {
    suspend fun getTimetable(): Result<List<Timetable>>
}