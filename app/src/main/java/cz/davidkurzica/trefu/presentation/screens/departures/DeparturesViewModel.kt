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
    val isLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): DeparturesUiState =
        if (!showResults) {
            if (isLoading || stops.isEmpty() || selectedStop == null) {
                DeparturesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Form.HasData(
                    selectedTime = selectedTime,
                    selectedStop = selectedStop,
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if (isLoading || departureWithLines.isEmpty()) {
                DeparturesUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Results.HasResults(
                    departureWithLines = departureWithLines,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
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
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = stopRepository.getStopOptions(forDate = LocalDate.now())

            viewModelState.update {
                when (result) {
                    is Result.Success -> {
                        val sorted = result.data.sortedBy { stop -> stop.name }
                        it.copy(
                            stops = sorted,
                            selectedStop = sorted.firstOrNull { stop -> stop.name == "Divadlo" } ?: sorted.first(),
                            isLoading = false
                        )
                    }
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

    fun submitForm(
        stopId: Int,
        after: LocalTime,
    ) {
        viewModelState.update { it.copy(showResults = true, isLoading = true) }

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
                        isLoading = false
                    )
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