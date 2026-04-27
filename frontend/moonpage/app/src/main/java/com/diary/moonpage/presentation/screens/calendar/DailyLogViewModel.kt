package com.diary.moonpage.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.core.util.ActivityPreferencesManager
import com.diary.moonpage.data.remote.api.DailyLogResponse
import com.diary.moonpage.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class DailyLogViewModel @Inject constructor(
    private val repository: DailyLogRepository,
    private val activityPreferencesManager: ActivityPreferencesManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _existingLog = MutableStateFlow<DailyLogResponse?>(null)
    val existingLog: StateFlow<DailyLogResponse?> = _existingLog.asStateFlow()

    // Enabled activity categories from DataStore
    val enabledCategories: StateFlow<Set<String>> = activityPreferencesManager.enabledCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ActivityPreferencesManager.DEFAULT_ENABLED
        )
        
    val dynamicActivities = activityPreferencesManager.activities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun fetchLogForDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getDailyLogByDate(date).onSuccess { log ->
                _existingLog.value = log
            }.onFailure {
                _existingLog.value = null
            }
            _isLoading.value = false
        }
    }

    fun saveDailyLog(
        date: String,
        baseMoodId: Int,
        note: String?,
        sleepHours: Double?,
        isMenstruation: Boolean,
        activityIds: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val baseMoodIdBody = baseMoodId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val dateBody = date.toRequestBody("text/plain".toMediaTypeOrNull())
            val noteBody = note?.toRequestBody("text/plain".toMediaTypeOrNull())
            val sleepHoursBody = sleepHours?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val isMenstruationBody = isMenstruation.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val activityParts = activityIds.map { id ->
                okhttp3.MultipartBody.Part.createFormData("ActivityIds", id.toString())
            }

            repository.createDailyLog(
                baseMoodIdBody,
                dateBody,
                noteBody,
                sleepHoursBody,
                isMenstruationBody,
                null,
                activityParts,
                null
            ).onSuccess {
                onSuccess()
            }.onFailure {
                // handle error
            }
            _isLoading.value = false
        }
    }
}
