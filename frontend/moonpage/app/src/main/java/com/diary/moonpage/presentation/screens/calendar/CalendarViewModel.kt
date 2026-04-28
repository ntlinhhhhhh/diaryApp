package com.diary.moonpage.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.data.remote.api.DailyLogResponse
import com.diary.moonpage.domain.repository.DailyLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

import com.diary.moonpage.core.util.ActivityPreferencesManager
import com.diary.moonpage.domain.model.Activity

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: DailyLogRepository,
    private val activityPreferencesManager: ActivityPreferencesManager
) : ViewModel() {

    val dynamicActivities: StateFlow<List<Activity>> = activityPreferencesManager.activities

    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _dailyLogs = MutableStateFlow<Map<LocalDate, DailyLogResponse>>(emptyMap())
    val dailyLogs: StateFlow<Map<LocalDate, DailyLogResponse>> = _dailyLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchLogsForMonth(_currentYearMonth.value)
    }

    fun changeMonth(offset: Long) {
        val newMonth = _currentYearMonth.value.plusMonths(offset)
        _currentYearMonth.value = newMonth
        fetchLogsForMonth(newMonth)
    }

    fun setYearMonth(year: Int, month: Int) {
        val newMonth = YearMonth.of(year, month)
        _currentYearMonth.value = newMonth
        fetchLogsForMonth(newMonth)
    }

    fun refreshLogs() {
        fetchLogsForMonth(_currentYearMonth.value)
    }

    private fun fetchLogsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            _isLoading.value = true
            val yearMonthStr = "${yearMonth.year}-${yearMonth.monthValue.toString().padStart(2, '0')}"
            
            repository.getDailyLogsByMonth(yearMonthStr).collect { logs ->
                val logsMap = logs.associateBy { LocalDate.parse(it.date) }
                val currentMap = _dailyLogs.value.toMutableMap()
                currentMap.keys.removeAll { YearMonth.from(it) == yearMonth }
                currentMap.putAll(logsMap)
                _dailyLogs.value = currentMap
                _isLoading.value = false // Stop loading once we have at least cache
            }
        }
    }

    fun deleteDailyLog(date: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteDailyLog(date).onSuccess {
                refreshLogs()
                onSuccess()
            }.onFailure {
                onFailure(it.message ?: "Failed to delete log")
            }
        }
    }
}
