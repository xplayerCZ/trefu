package cz.davidkurzica.trefu.ui.screens.timetables

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.DirectionOptionsQuery
import cz.davidkurzica.trefu.LineOptionsQuery
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.StopOptionsQuery
import cz.davidkurzica.trefu.adapter.queryResult
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.model.*
import cz.davidkurzica.trefu.util.ErrorMessage
import cz.davidkurzica.trefu.util.toLine
import cz.davidkurzica.trefu.util.toStop
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

sealed interface TimetablesUiState {

    val isResultsOpen: Boolean
    val errorMessages: List<ErrorMessage>

    sealed interface Form : TimetablesUiState {
        val isLoading: Boolean

        data class HasData(
            val formData: TimetablesFormData,
            val stops: List<Stop>,
            val lines: List<Line>,
            val directions: List<Direction>,
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>
        ) : Form

        data class NoData(
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>
        ) : Form
    }


    sealed interface Results : TimetablesUiState {
        val isLoading: Boolean

        data class HasResults(
            val timetables: Timetable,
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>,
        ) : Results
        data class NoResults(
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>,
        ) : Results
    }
}

private data class TimetablesViewModelState(
    val selectedStop: Stop? = null,
    val selectedLine: Line? = null,
    val selectedDirection: Direction? = null,
    val stops: List<Stop> = emptyList(),
    val lines: List<Line> = emptyList(),
    val directions: List<Direction> = emptyList(),
    val timetables: Timetable? = null,
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): TimetablesUiState =
        if (!showResults) {
            if (isFormLoading || stops.isEmpty() || lines.isEmpty() || directions.isEmpty()) {
                TimetablesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                TimetablesUiState.Form.HasData(
                    formData = TimetablesFormData(
                        selectedStop = selectedStop ?: stops.first(),
                        selectedLine = selectedLine ?: lines.first(),
                        selectedDirection = selectedDirection ?: directions.first()
                    ),
                    stops = stops,
                    lines = lines,
                    directions = directions,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if(isResultsLoading || timetables == null) {
                TimetablesUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                TimetablesUiState.Results.HasResults(
                    timetables = timetables,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class TimetablesViewModel(
    private val apolloClient: ApolloClient
) : ViewModel() {

    private val viewModelState = MutableStateFlow(TimetablesViewModelState())

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    init {
        launchWithLoading { updateStops() }
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    private suspend fun updateStops() {
        val result = apolloClient.queryResult(StopOptionsQuery())
        viewModelState.update {
            when (result) {
                is Result.Success -> it.copy(stops = result.data.stops.map { orig -> orig.toStop() })
                is Result.Error -> {
                    val errorMessages = it.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = R.string.load_error
                    )
                    it.copy(errorMessages = errorMessages)
                }
            }
        }

        viewModelState.value.stops.firstOrNull()?.let { updateLines(it.id) }
    }

    private suspend fun updateLines(stopId: Int) {
        val result = apolloClient.queryResult(LineOptionsQuery(stopId = stopId.toString()))

        viewModelState.update {
            when (result) {
                is Result.Success -> it.copy(
                    lines = result.data.stop.routeStops.map { routeStop -> routeStop.route.line.toLine() },
                    selectedLine = null,
                )
                is Result.Error -> {
                    val errorMessages = it.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = R.string.load_error
                    )
                    it.copy(errorMessages = errorMessages)
                }
            }
        }

        viewModelState.value.lines.firstOrNull()?.let { updateDirections(it.id) }
    }

    private suspend fun updateDirections(lineId: Int) {
        val result = apolloClient.queryResult(DirectionOptionsQuery(lineId.toString()))

        viewModelState.update {
            when (result) {
                is Result.Success -> it.copy(
                    directions = result.data.line.routes.mapIndexed { i, route ->
                        Direction(
                            i,
                            route.lastRouteStop.first().stop.name
                        )
                    },
                    selectedDirection = null,
                )
                is Result.Error -> {
                    val errorMessages = it.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = R.string.load_error
                    )
                    it.copy(errorMessages = errorMessages)
                }
            }
        }

        Log.d("Directions", viewModelState.value.directions.toString())
    }

    fun submitForm(formData: TimetablesFormData, date: LocalDate = LocalDate.now()) {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(
                Timetable(
                    date = LocalDate.now(),
                    lineShortCode = "208",
                    departures = listOf()
                )
            ) as Result<Timetable>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(timetables = result.data, isResultsLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isResultsLoading = false)
                    }
                }
            }
        }
    }

    fun cleanForm() {
        TODO("Not yet implemented")
    }

    fun closeResults() {
        viewModelState.update { it.copy(showResults = false) }
    }

    fun updateLine(line: Line) {
        launchWithLoading { updateDirections(line.id) }
    }

    fun updateDirection(direction: Direction) {
        viewModelState.update { it.copy(selectedDirection = direction) }
    }

    fun updateStop(stop: Stop) {
        launchWithLoading { updateLines(stop.id) }
    }

    private fun launchWithLoading(block: suspend () -> Unit) {
        viewModelState.update { it.copy(isFormLoading = true) }
        viewModelScope.launch {
            block()
            viewModelState.update { it.copy(isFormLoading = false) }
        }
    }

    companion object {
        fun provideFactory(
            apolloClient: ApolloClient
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimetablesViewModel(apolloClient) as T
            }
        }
    }
}