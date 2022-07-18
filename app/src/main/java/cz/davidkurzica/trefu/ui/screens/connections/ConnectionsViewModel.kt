package cz.davidkurzica.trefu.ui.screens.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.StopOptionsQuery
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.model.Connection
import cz.davidkurzica.trefu.model.Stop
import cz.davidkurzica.trefu.util.ErrorMessage
import cz.davidkurzica.trefu.util.toStop
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

sealed interface ConnectionsUiState {

    val isResultsOpen: Boolean
    val errorMessages: List<ErrorMessage>

    sealed interface Form : ConnectionsUiState {
        val isLoading: Boolean

        data class HasData(
            val selectedFromStop: Stop,
            val selectedToStop: Stop,
            val selectedTime: LocalTime,
            val stops: List<Stop>,
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
    val selectedFromStop: Stop? = null,
    val selectedToStop: Stop? = null,
    val selectedTime: LocalTime = LocalTime.now(),
    val stops: List<Stop> = emptyList(),
    val connections: List<Connection> = emptyList(),
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): ConnectionsUiState =
        if (!showResults) {
            if (isFormLoading || stops.isEmpty()) {
                ConnectionsUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                ConnectionsUiState.Form.HasData(
                    selectedFromStop = selectedFromStop ?: stops.first(),
                    selectedToStop = selectedToStop ?: stops.first(),
                    selectedTime = selectedTime,
                    stops = stops,
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            }
        } else {
            if (isResultsLoading) {
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
    private val apolloClient: ApolloClient,
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
            val result = try {
                Result.Success(apolloClient.query(StopOptionsQuery()).execute().dataAssertNoErrors)
            } catch (exception: ApolloException) {
                Result.Error(exception)
            }

            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        stops = result.data.stops.map { stop -> stop.toStop() },
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

    fun submitForm() {
        viewModelState.update { it.copy(showResults = true, isResultsLoading = true) }

        viewModelScope.launch {
            val result = Result.Success(listOf<Connection>()) as Result<List<Connection>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(
                        connections = result.data,
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

    fun updateFromStop(stop: Stop) {
        viewModelState.update { it.copy(selectedFromStop = stop) }
    }

    fun updateToStop(stop: Stop) {
        viewModelState.update { it.copy(selectedToStop = stop) }
    }

    fun updateTime(time: LocalTime) {
        viewModelState.update { it.copy(selectedTime = time) }
    }

    companion object {
        fun provideFactory(
            apolloClient: ApolloClient,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ConnectionsViewModel(apolloClient) as T
            }
        }
    }
}