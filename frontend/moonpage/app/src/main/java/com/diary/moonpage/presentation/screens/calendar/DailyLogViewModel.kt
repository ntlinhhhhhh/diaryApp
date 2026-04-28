package com.diary.moonpage.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.core.util.ActivityPreferencesManager
import com.diary.moonpage.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val repository: DailyLogRepository,
    private val activityPreferencesManager: ActivityPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyLogUiState())
    val uiState: StateFlow<DailyLogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            activityPreferencesManager.enabledCategories.collect { categories ->
                _uiState.update { it.copy(enabledCategories = categories.toList()) }
            }
        }
        viewModelScope.launch {
            activityPreferencesManager.activities.collect { activities ->
                _uiState.update { it.copy(dynamicActivities = activities) }
            }
        }
    }

    fun onEvent(event: DailyLogUiEvent) {
        when (event) {
            is DailyLogUiEvent.OnMoodSelected -> {
                _uiState.update { it.copy(selectedMood = event.moodId) }
            }
            is DailyLogUiEvent.OnActivityToggled -> {
                _uiState.update { state ->
                    val newList = if (state.selectedActivities.contains(event.activityId)) {
                        state.selectedActivities - event.activityId
                    } else {
                        state.selectedActivities + event.activityId
                    }
                    state.copy(selectedActivities = newList)
                }
            }
            is DailyLogUiEvent.OnNoteChanged -> {
                _uiState.update { it.copy(noteText = event.note) }
            }
            is DailyLogUiEvent.OnSleepChanged -> {
                _uiState.update { it.copy(sleepHours = event.hours) }
            }
            is DailyLogUiEvent.OnDateChanged -> {
                _uiState.update { it.copy(date = event.date) }
                fetchLogForDate(event.date)
            }
            DailyLogUiEvent.OnSaveClick -> {
                saveDailyLog()
            }
            DailyLogUiEvent.OnExitClick -> {
                _uiState.update { it.copy(showExitDialog = true) }
            }
            DailyLogUiEvent.OnDismissExitDialog -> {
                _uiState.update { it.copy(showExitDialog = false) }
            }
            DailyLogUiEvent.OnDismissOverwriteDialog -> {
                _uiState.update { it.copy(showOverwriteDialog = false) }
            }
            DailyLogUiEvent.OnConfirmOverwrite -> {
                _uiState.value.pendingDate?.let { date ->
                    _uiState.update { it.copy(date = date, showOverwriteDialog = false) }
                    fetchLogForDate(date)
                }
            }
            DailyLogUiEvent.OnDatePickerClick -> {
                _uiState.update { it.copy(showDatePicker = true) }
            }
            DailyLogUiEvent.OnDatePickerDismiss -> {
                _uiState.update { it.copy(showDatePicker = false) }
            }
            DailyLogUiEvent.DismissMessage -> {
                _uiState.update { it.copy(snackbarMessage = null) }
            }
        }
    }

    fun setInitialDate(date: LocalDate) {
        _uiState.update { it.copy(date = date) }
        fetchLogForDate(date)
    }

    private fun fetchLogForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getDailyLogByDate(date.toString()).onSuccess { log ->
                _uiState.update { it.copy(
                    existingLog = log,
                    selectedMood = log.baseMoodId,
                    selectedActivities = log.activityIds ?: emptyList(),
                    noteText = log.note ?: "",
                    sleepHours = log.sleepHours?.toFloat() ?: 7f,
                    isLoading = false
                ) }
            }.onFailure {
                _uiState.update { it.copy(
                    existingLog = null,
                    selectedMood = null,
                    selectedActivities = emptyList(),
                    noteText = "",
                    sleepHours = 7f,
                    isLoading = false
                ) }
            }
        }
    }

    fun checkLogExists(date: LocalDate, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.getDailyLogByDate(date.toString()).onSuccess {
                onResult(true)
            }.onFailure {
                onResult(false)
            }
        }
    }

    fun setPendingDate(date: LocalDate) {
        _uiState.update { it.copy(pendingDate = date, showOverwriteDialog = true) }
    }

    private var _onSaveSuccess: ((String) -> Unit)? = null
    fun setOnSaveSuccess(callback: (String) -> Unit) {
        _onSaveSuccess = callback
    }

    private fun saveDailyLog() {
        val state = _uiState.value
        if (state.selectedMood == null) {
            _uiState.update { it.copy(snackbarMessage = "Please select a mood first!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val baseMoodIdBody = state.selectedMood.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val dateBody = state.date.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val noteBody = state.noteText.takeIf { it.isNotBlank() }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val sleepHoursBody = state.sleepHours.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val isMenstruationBody = "false".toRequestBody("text/plain".toMediaTypeOrNull())
            val activityParts = state.selectedActivities.map { id ->
                okhttp3.MultipartBody.Part.createFormData("ActivityIds", id)
            }

            repository.createDailyLog(
                state.date.toString(),
                baseMoodIdBody,
                dateBody,
                noteBody,
                sleepHoursBody,
                isMenstruationBody,
                null,
                activityParts,
                null
            ).onSuccess {
                val msg = if (state.existingLog != null) "Record updated successfully!" else "Record created successfully!"
                _onSaveSuccess?.invoke(msg)
            }.onFailure { error ->
                _uiState.update { it.copy(snackbarMessage = error.message ?: "Failed to save log", isLoading = false) }
            }
        }
    }
}
