package cz.davidkurzica.trefu.presentation.screens.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.domain.*
import cz.davidkurzica.trefu.domain.repository.RouteRepository
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.repository.TimetableRepository
import cz.davidkurzica.trefu.domain.util.ErrorMessage
import cz.davidkurzica.trefu.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

sealed interface TimetablesUiState {

    val isResultsOpen: Boolean
    val errorMessages: List<ErrorMessage>

    sealed interface Form : TimetablesUiState {
        val isLoading: Boolean

        data class HasData(
            val selectedStop: StopOption,
            val selectedLine: Line,
            val selectedDirection: RouteDirection,
            val stops: List<StopOption>,
            val lines: List<Line>,
            val directions: List<RouteDirection>,
            val routes: List<Route>,
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
    val selectedStop: StopOption? = null,
    val selectedLine: Line? = null,
    val selectedDirection: RouteDirection? = null,
    val stops: List<StopOption> = emptyList(),
    val lines: List<Line> = emptyList(),
    val directions: List<RouteDirection> = emptyList(),
    val routes: List<Route> = emptyList(),
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
                    routes = routes,
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
    private val timetableRepository: TimetableRepository,
    private val stopRepository: StopRepository,
    private val routeRepository: RouteRepository,
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
        val result = stopRepository.getStopOptions(forDate = LocalDate.now())
        viewModelState.update {
            when (result) {
                is Result.Success -> it.copy(stops = result.data.sortedBy { stop -> stop.name })
                is Result.Error -> {
                    val errorMessages = it.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = R.string.load_error
                    )
                    it.copy(errorMessages = errorMessages)
                }
            }
        }

        viewModelState.value.stops.firstOrNull()?.let { updateRoutes(it.id) }
    }

    private suspend fun updateRoutes(stopId: Int) {
        val result = routeRepository.getRouteOptions(forDate = LocalDate.now(), stopId)

        viewModelState.update {
            when (result) {
                is Result.Success -> {
                    val routes = result.data
                    it.copy(
                        routes = routes,
                        lines = routes.map { route -> route.line }.distinct(),
                        directions = routes.map { route -> route.direction },
                        selectedLine = null,
                        selectedDirection = null,
                    )
                }
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

    fun submitForm(
        stopId: Int,
        routeId: Int,
        lineShortCode: String,
    ) {
        viewModelState.update { it.copy(showResults = true, isLoading = true) }

        viewModelScope.launch {
            val result = timetableRepository.getTimetable(
                forDate = LocalDate.now(),
                after = LocalTime.MIDNIGHT,
                stopId = stopId,
                routeId = routeId,
                lineShortCode = lineShortCode,
            )

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

    fun refreshForm() {
        viewModelScope.launchWithLoading { updateStops() }
    }

    fun closeResults() {
        viewModelState.update { it.copy(showResults = false) }
    }

    fun updateLine(line: Line) {
        viewModelState.update { state ->
            val directions = state.routes.filter { it.line == line }.map { it.direction }
            state.copy(
                selectedLine = line,
                directions = directions,
                selectedDirection = directions.first()
            )
        }
    }

    fun updateDirection(direction: RouteDirection) {
        viewModelState.update { it.copy(selectedDirection = direction) }
    }

    fun updateStop(stop: StopOption) {
        viewModelState.update { it.copy(selectedStop = stop) }
        viewModelScope.launchWithLoading { updateRoutes(stop.id) }
    }

    private fun CoroutineScope.launchWithLoading(block: suspend () -> Unit) {
        viewModelState.update { it.copy(isLoading = true) }
        this.launch {
            block()
            viewModelState.update { it.copy(isLoading = false) }
        }
    }
}