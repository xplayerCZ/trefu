package cz.davidkurzica.trefu.ui.screens.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.data.connections.ConnectionService
import cz.davidkurzica.trefu.data.tracks.FormService
import cz.davidkurzica.trefu.model.Connection
import cz.davidkurzica.trefu.model.ConnectionsFormData
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.util.ErrorMessage
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
            val formData: ConnectionsFormData,
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


    sealed interface Results : ConnectionsUiState {
        val isLoading: Boolean

        data class HasResults(
            val connections: List<Connection>,
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
    val selectedStopFrom: Stop? = null,
    val selectedTimeFrom: LocalTime = LocalTime.now(),
    val selectedStopTo: Stop? = null,
    val selectedTimeTo: LocalTime = LocalTime.now(),
    val stops: List<Stop> = emptyList(),
    val connections: List<Connection> = emptyList(),
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): ConnectionsUiState =
        if (!showResults) {
            if(isFormLoading || stops.isEmpty()) {
                ConnectionsUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                ConnectionsUiState.Form.HasData(
                    formData = ConnectionsFormData(
                        selectedStopFrom = selectedStopFrom ?: stops[0],
                        selectedTimeFrom = selectedTimeFrom,
                        selectedStopTo = selectedStopTo ?: stops[0],
                        selectedTimeTo = selectedTimeTo,
                    ),
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if(isResultsLoading) {
                ConnectionsUiState.Results.NoResults(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                ConnectionsUiState.Results.HasResults(
                    connections = connections,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        }
}

class ConnectionsViewModel(
    private val connectionService: ConnectionService,
    private val FormService: FormService
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ConnectionsViewModelState())

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
            val result = Result.Success(FormService.getStops()) as Result<List<Stop>>
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

    fun submitForm(formData: ConnectionsFormData, date: LocalDate = LocalDate.now()) {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(connectionService.getConnections(
                formData.selectedStopFrom.id,
                formData.selectedTimeFrom,
                formData.selectedStopTo.id,
                formData.selectedTimeTo,
                date
            )) as Result<List<Connection>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(connections = result.data, isResultsLoading = false)
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

    fun updateForm(formData: ConnectionsFormData) {
        viewModelState.update { it.copy(
            selectedStopFrom = formData.selectedStopFrom,
            selectedTimeFrom = formData.selectedTimeFrom,
            selectedStopTo = formData.selectedStopTo,
            selectedTimeTo = formData.selectedTimeTo,
        ) }
    }

    companion object {
        fun provideFactory(
            connectionService: ConnectionService,
            FormService: FormService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ConnectionsViewModel(connectionService, FormService) as T
            }
        }
    }
}