package cz.davidkurzica.trefu.presentation.screens.departures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.domain.DepartureWithLine
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.repository.DepartureRepository
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.util.ErrorMessage
import cz.davidkurzica.trefu.domain.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

sealed interface DeparturesUiState {

    val isResultsOpen: Boolean
    val errorMessages: List<ErrorMessage>

    sealed interface Form : DeparturesUiState {
        val isLoading: Boolean

        data class HasData(
            val selectedTime: LocalTime,
            val selectedStop: StopOption,
            val stops: List<StopOption>,
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


    sealed interface Results : DeparturesUiState {
        val isLoading: Boolean

        data class HasResults(
            val departureWithLines: List<DepartureWithLine>,
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

private data class DeparturesViewModelState(
    val selectedTime: LocalTime = LocalTime.now(),
    val selectedStop: StopOption? = null,
    val stops: List<StopOption> = emptyList(),
    val departureWithLines: List<DepartureWithLine> = emptyList(),
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): DeparturesUiState =
        if (!showResults) {
            if (isFormLoading || stops.isEmpty()) {
                DeparturesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Form.HasData(
                    selectedTime = selectedTime,
                    selectedStop = selectedStop ?: stops[0],
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if (isResultsLoading) {
                DeparturesUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Results.HasResults(
                    departureWithLines = departureWithLines,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class DeparturesViewModel(
    private val stopRepository: StopRepository,
    private val departureRepository: DepartureRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(DeparturesViewModelState())

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    init {
        loadForm()
    }

    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    fun loadForm() {
        viewModelState.update { it.copy(isFormLoading = true) }

        viewModelScope.launch {
            val result = stopRepository.getStopOptions(forDate = LocalDate.now())

            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        stops = result.data.sortedBy { stop -> stop.name },
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

    fun submitForm(
        stopId: Int,
        after: LocalTime,
    ) {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = departureRepository.getDepartures(
                limit = 15,
                stopId = stopId,
                after = after,
                forDate = LocalDate.now()
            )
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        departureWithLines = result.data,
                        isResultsLoading = false
                    )
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

    fun refreshForm() {
        loadForm()
    }

    fun closeResults() {
        viewModelState.update { it.copy(showResults = false) }
    }

    fun updateStop(stop: StopOption) {
        viewModelState.update { it.copy(selectedStop = stop) }
    }

    fun updateTime(time: LocalTime) {
        viewModelState.update { it.copy(selectedTime = time) }
    }
}