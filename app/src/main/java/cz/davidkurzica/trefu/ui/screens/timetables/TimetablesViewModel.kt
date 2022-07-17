package cz.davidkurzica.trefu.ui.screens.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import cz.davidkurzica.trefu.DirectionOptionsQuery
import cz.davidkurzica.trefu.LineOptionsQuery
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.StopOptionsQuery
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.model.Direction
import cz.davidkurzica.trefu.model.Line
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.model.Timetable
import cz.davidkurzica.trefu.util.*
import kotlinx.coroutines.CoroutineScope
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
            val selectedStop: Stop,
            val selectedLine: Line,
            val selectedDirection: Direction,
            val stops: List<Stop>,
            val lines: List<Line>,
            val directions: List<Direction>,
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>,
        ) : Form

        data class NoData(
            override val isResultsOpen: Boolean,
            override val isLoading: Boolean,
            override val errorMessages: List<ErrorMessage>,
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
    val isLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): TimetablesUiState =
        if (!showResults) {
            if (isLoading || stops.isEmpty() || lines.isEmpty() || directions.isEmpty()) {
                TimetablesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                TimetablesUiState.Form.HasData(
                    selectedStop = selectedStop ?: stops.first(),
                    selectedLine = selectedLine ?: lines.first(),
                    selectedDirection = selectedDirection ?: directions.first(),
                    stops = stops,
                    lines = lines,
                    directions = directions,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if (isLoading || timetables == null) {
                TimetablesUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                TimetablesUiState.Results.HasResults(
                    timetables = timetables,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class TimetablesViewModel(
    private val apolloClient: ApolloClient,
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
        viewModelScope.launchWithLoading { updateStops() }
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
                is Result.Success -> it.copy(stops = result.data.toStops())
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
                    lines = result.data.toLines(),
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
                    directions = result.data.toDirections(),
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
    }

    fun submitForm() {
        viewModelState.update { it.copy(showResults = true, isLoading = true) }

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
                    is Result.Success -> it.copy(timetables = result.data, isLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
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
        viewModelState.update { it.copy(selectedLine = line) }
        viewModelScope.launchWithLoading { updateDirections(line.id) }
    }

    fun updateDirection(direction: Direction) {
        viewModelState.update { it.copy(selectedDirection = direction) }
    }

    fun updateStop(stop: Stop) {
        viewModelState.update { it.copy(selectedStop = stop) }
        viewModelScope.launchWithLoading { updateLines(stop.id) }
    }

    private fun CoroutineScope.launchWithLoading(block: suspend () -> Unit) {
        viewModelState.update { it.copy(isLoading = true) }
        this.launch {
            block()
            viewModelState.update { it.copy(isLoading = false) }
        }
    }

    companion object {
        fun provideFactory(
            apolloClient: ApolloClient,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimetablesViewModel(apolloClient) as T
            }
        }
    }
}