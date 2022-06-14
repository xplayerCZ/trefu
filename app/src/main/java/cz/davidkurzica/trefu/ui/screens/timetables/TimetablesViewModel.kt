package cz.davidkurzica.trefu.ui.screens.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.data.timetables.TimetableService
import cz.davidkurzica.trefu.data.tracks.FormService
import cz.davidkurzica.trefu.model.*
import cz.davidkurzica.trefu.util.ErrorMessage
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
                        selectedStop = selectedStop ?: stops[0],
                        selectedLine = selectedLine ?: lines[0],
                        selectedDirection = selectedDirection ?: directions[0]
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
    private val timetableService: TimetableService,
    private val formService: FormService
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
        loadStops()
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    fun loadDirections(lineId: Int) {
        viewModelState.update { it.copy(isFormLoading = true) }

        viewModelScope.launch {
            val result =
                Result.Success(formService.getDirections(lineId)) as Result<List<Direction>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        directions = result.data,
                        selectedDirection = null,
                        isFormLoading = false
                    )
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isFormLoading = false)
                    }
                }
            }
        }
    }

    fun loadLines(stopId: Int) {
        viewModelState.update { it.copy(isFormLoading = true) }

        viewModelScope.launch {
            val result =
                Result.Success(formService.getLines(stopId, LocalDate.now())) as Result<List<Line>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        lines = result.data,
                        selectedLine = null,
                        isFormLoading = false
                    )
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isFormLoading = false)
                    }
                }
            }
            if (result is Result.Success) {
                loadDirections(result.data[0].id)
            }
        }
    }

    fun loadStops() {
        viewModelState.update { it.copy(isFormLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(formService.getStops()) as Result<List<Stop>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(stops = result.data, isFormLoading = false)
                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isFormLoading = false)
                    }
                }
            }
            if (result is Result.Success) {
                loadLines(result.data[0].id)
            }
        }
    }

    fun submitForm(formData: TimetablesFormData, date: LocalDate = LocalDate.now()) {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(
                timetableService.getTimetable(
                    formData.selectedStop.id,
                    formData.selectedLine.id,
                    formData.selectedDirection.id,
                    date
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
        viewModelState.update { it.copy(selectedLine = line) }

        loadDirections(line.id)
    }

    fun updateDirection(direction: Direction) {
        viewModelState.update { it.copy(selectedDirection = direction) }
    }

    fun updateStop(stop: Stop) {
        viewModelState.update { it.copy(selectedStop = stop) }

        loadLines(stop.id)
    }

    companion object {
        fun provideFactory(
            timetableService: TimetableService,
            formService: FormService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimetablesViewModel(timetableService, formService) as T
            }
        }
    }
}