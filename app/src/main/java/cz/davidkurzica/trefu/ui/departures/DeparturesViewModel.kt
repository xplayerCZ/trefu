package cz.davidkurzica.trefu.ui.departures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.util.ErrorMessage
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.data.departures.DeparturesService
import cz.davidkurzica.trefu.data.tracks.StopService
import cz.davidkurzica.trefu.model.Stop
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
            val selectedStop: Stop,
            val stops: List<Stop>,
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


    sealed interface Results : DeparturesUiState {
        val isLoading: Boolean

        data class HasResults(
            val departures: List<Departure>,
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
    val selectedStop: Stop? = null,
    val stops: List<Stop> = emptyList(),
    val departures: List<Departure> = emptyList(),
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): DeparturesUiState =
        if (!showResults) {
            if(isFormLoading || stops.isEmpty()) {
                DeparturesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Form.HasData(
                    selectedStop = selectedStop ?: stops[0],
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if(isResultsLoading) {
                DeparturesUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                DeparturesUiState.Results.HasResults(
                    departures = departures,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class DeparturesViewModel(
    private val departuresService: DeparturesService,
    private val stopService: StopService
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
            val result = Result.Success(stopService.getStops()) as Result<List<Stop>>
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
        }
    }

    fun submitForm(stopId: Int, time: LocalTime = LocalTime.now(), date: LocalDate = LocalDate.now()) {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(departuresService.getDepartures(stopId, time, date)) as Result<List<Departure>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(departures = result.data, isResultsLoading = false)
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

    fun updateForm(stop: Stop) {
        viewModelState.update { it.copy(selectedStop = stop) }
    }


    companion object {
        fun provideFactory(
            departuresService: DeparturesService,
            stopService: StopService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeparturesViewModel(departuresService, stopService) as T
            }
        }
    }
}