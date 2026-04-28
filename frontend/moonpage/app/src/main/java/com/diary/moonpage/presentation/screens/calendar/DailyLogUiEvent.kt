package com.diary.moonpage.presentation.screens.calendar

import java.time.LocalDate

sealed interface DailyLogUiEvent {
    data class OnMoodSelected(val moodId: Int) : DailyLogUiEvent
    data class OnActivityToggled(val activityId: String) : DailyLogUiEvent
    data class OnNoteChanged(val note: String) : DailyLogUiEvent
    data class OnSleepChanged(val hours: Float) : DailyLogUiEvent
    data class OnDateChanged(val date: LocalDate) : DailyLogUiEvent
    object OnSaveClick : DailyLogUiEvent
    object OnExitClick : DailyLogUiEvent
    object OnDismissExitDialog : DailyLogUiEvent
    object OnDismissOverwriteDialog : DailyLogUiEvent
    object OnConfirmOverwrite : DailyLogUiEvent
    object OnDatePickerClick : DailyLogUiEvent
    object OnDatePickerDismiss : DailyLogUiEvent
    object DismissMessage : DailyLogUiEvent
}
