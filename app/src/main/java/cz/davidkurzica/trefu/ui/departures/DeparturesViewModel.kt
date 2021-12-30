package cz.davidkurzica.trefu.ui.departures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Departure
import cz.davidkurzica.trefu.util.ErrorMessage
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.data.departures.DeparturesService
import cz.davidkurzica.trefu.data.tracks.TrackService
import cz.davidkurzica.trefu.model.DeparturesForm
import cz.davidkurzica.trefu.model.Track
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalTime
import java.util.*

sealed interface DeparturesUiState {

    val isResultsOpen: Boolean
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoDepartures(
        override val isResultsOpen: Boolean,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : DeparturesUiState


    data class HasDepartures(
        val form: DeparturesForm,
        val departures: List<Departure>,
        override val isResultsOpen: Boolean,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
    ) : DeparturesUiState
}

private data class DeparturesViewModelState(
    val form: DeparturesForm? = null,
    val departures: List<Departure> = emptyList(),
    val isLoading: Boolean = false,
    val isResultsOpen: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): DeparturesUiState =
        if (form == null /* || departures.isEmpty() */) {
            DeparturesUiState.NoDepartures(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isResultsOpen = isResultsOpen
            )
        } else {
            DeparturesUiState.HasDepartures(
                form = form,
                departures = departures,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isResultsOpen = isResultsOpen
            )
        }
}

class DeparturesViewModel(
    private val departuresService: DeparturesService,
    private val trackService: TrackService
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
        viewModelScope.launch {
            val result = Result.Success(trackService.getTracks()) as Result<List<Track>>
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(form = DeparturesForm(result.data), isLoading = false)
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

    fun submitForm(stopId: Int, time: LocalTime) {
        viewModelState.update { it.copy(isResultsOpen = true, isLoading = true) }

        viewModelScope.launch {
            val result = departuresService.getDepartures(stopId, time)
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(departures = result.data, isLoading = false)
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
        viewModelState.update { it.copy(isResultsOpen = false) }
    }


    companion object {
        fun provideFactory(
            departuresService: DeparturesService,
            trackService: TrackService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DeparturesViewModel(departuresService, trackService) as T
            }
        }
    }
}