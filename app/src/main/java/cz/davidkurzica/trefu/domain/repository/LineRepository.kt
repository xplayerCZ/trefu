package cz.davidkurzica.trefu.domain.repository

import cz.davidkurzica.trefu.domain.Line
import cz.davidkurzica.trefu.domain.util.Result

interface LineRepository {
    suspend fun getLines(id: Int): Result<Line>
}