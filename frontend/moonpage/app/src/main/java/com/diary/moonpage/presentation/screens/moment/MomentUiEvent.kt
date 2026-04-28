package com.diary.moonpage.presentation.screens.moment

import com.diary.moonpage.core.util.UiText
import java.io.File

sealed class MomentUiEvent {
    object LoadMoments : MomentUiEvent()
    data class LoadMomentDetail(val id: String) : MomentUiEvent()
    data class UploadMoment(
        val imageFile: File,
        val caption: String,
        val location: String? = null,
        val weather: String? = null,
        val rating: Float? = null,
        val dailyLogId: String = "default_log_id",
        val isPublic: Boolean = true,
        val onSuccess: () -> Unit = {}
    ) : MomentUiEvent()
    data class DeleteMoment(val id: String) : MomentUiEvent()
    data class DownloadMoment(val imageUrl: String) : MomentUiEvent()
    data class ShareMoment(val url: String) : MomentUiEvent()
    object DismissMessage : MomentUiEvent()
    data class ShowSnackBar(val message: UiText) : MomentUiEvent()
}
