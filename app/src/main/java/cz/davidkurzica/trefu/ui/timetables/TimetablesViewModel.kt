package cz.davidkurzica.trefu.ui.timetables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.davidkurzica.trefu.R
import cz.davidkurzica.trefu.model.Timetable
import cz.davidkurzica.trefu.util.ErrorMessage
import cz.davidkurzica.trefu.data.Result
import cz.davidkurzica.trefu.data.timetables.TimetableService
import cz.davidkurzica.trefu.data.tracks.StopService
import cz.davidkurzica.trefu.model.Stop
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
    val stops: List<Stop> = emptyList(),
    val timetables: Timetable? = null,
    val isFormLoading: Boolean = false,
    val isResultsLoading: Boolean = false,
    val showResults: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
) {

    fun toUiState(): TimetablesUiState =
        if (!showResults) {
            if(isFormLoading || stops.isEmpty()) {
                TimetablesUiState.Form.NoData(
                    isResultsOpen = showResults,
                    isLoading = isFormLoading,
                    errorMessages = errorMessages
                )
            } else {
                TimetablesUiState.Form.HasData(
                    selectedStop = selectedStop ?: stops[0],
                    stops = stops,
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
    private val stopService: StopService
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
            val result = Result.Success(timetableService.getTimetable(stopId, time, date)) as Result<Timetable>
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

    fun updateForm(stop: Stop) {
        viewModelState.update { it.copy(selectedStop = stop) }
    }


    companion object {
        fun provideFactory(
            timetableService: TimetableService,
            stopService: StopService,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TimetablesViewModel(timetableService, stopService) as T
            }
        }
    }
}