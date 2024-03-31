package cz.davidkurzica.trefu.presentation.screens.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.domain.ConnectionSet
import cz.davidkurzica.trefu.domain.StopOption
import cz.davidkurzica.trefu.domain.repository.ConnectionRepository
import cz.davidkurzica.trefu.domain.repository.StopRepository
import cz.davidkurzica.trefu.domain.util.ErrorMessage
import cz.davidkurzica.trefu.domain.util.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

sealed interface ConnectionsUiState {

    val isResultsOpen: Boolean
    val errorMessages: List<ErrorMessage>

    sealed interface Form : ConnectionsUiState {
        val isLoading: Boolean

        data class HasData(
            val selectedFromStop: StopOption,
            val selectedToStop: StopOption,
            val selectedTime: LocalTime,
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


    sealed interface Results : ConnectionsUiState {
        val isLoading: Boolean

        data class HasResults(
            val connectionSets: List<ConnectionSet>,
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

private data class ConnectionsViewModelState(
    val selectedFromStop: StopOption? = null,
    val selectedToStop: StopOption? = null,
    val selectedTime: LocalTime = LocalTime.now(),
    val stops: List<StopOption> = emptyList(),
    val connectionSets: List<ConnectionSet> = emptyList(),
    val isLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): ConnectionsUiState =
        if (!showResults) {
            if (isLoading || stops.isEmpty() || selectedFromStop == null || selectedToStop == null) {
                ConnectionsUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                ConnectionsUiState.Form.HasData(
                    selectedFromStop = selectedFromStop,
                    selectedToStop = selectedToStop,
                    selectedTime = selectedTime,
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if (isLoading || connectionSets.isEmpty()) {
                ConnectionsUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            } else {
                ConnectionsUiState.Results.HasResults(
                    connectionSets = connectionSets,
                    isResultsOpen = showResults,
                    isLoading = isLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class ConnectionsViewModel(
    private val stopRepository: StopRepository,
    private val connectionRepository: ConnectionRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ConnectionsViewModelState())

    val uiState = viewModelState
        .onEach { println(it) }
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
                            selectedFromStop = sorted.firstOrNull(),
                            selectedToStop = sorted.lastOrNull(),
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

    fun submitForm() {
        viewModelState.update { it.copy(showResults = true, isLoading = true) }

        viewModelScope.launch {
            val result = connectionRepository.getConnectionSets(
                fromStopId = viewModelState.value.selectedFromStop?.id ?: error("From stop not selected"),
                toStopId = viewModelState.value.selectedToStop?.id ?: error("To stop not selected"),
                after = viewModelState.value.selectedTime
            )
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        connectionSets = result.data,
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

    fun updateFromStop(stop: StopOption) {
        viewModelState.update { it.copy(selectedFromStop = stop) }
    }

    fun updateToStop(stop: StopOption) {
        viewModelState.update { it.copy(selectedToStop = stop) }
    }

    fun updateTime(time: LocalTime) {
        viewModelState.update { it.copy(selectedTime = time) }
    }
}