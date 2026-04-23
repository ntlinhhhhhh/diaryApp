package com.diary.moonpage.presentation.screens.moment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.domain.repository.MomentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MomentViewModel @Inject constructor(
    private val repository: MomentRepository
) : ViewModel() {

    private val _moments = MutableStateFlow<List<MomentResponse>>(emptyList())
    val moments: StateFlow<List<MomentResponse>> = _moments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchMyMoments()
    }

    fun fetchMyMoments() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMyMoments().onSuccess {
                _moments.value = it
            }.onFailure {
                // Handle error
            }
            _isLoading.value = false
        }
    }

    fun uploadMoment(
        imageFile: File,
        caption: String,
        dailyLogId: String = "default_log_id", // Should be passed from UI
        isPublic: Boolean = true
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val imagePart = MultipartBody.Part.createFormData(
                "imageFile",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            
            val dailyLogIdBody = dailyLogId.toRequestBody("text/plain".toMediaTypeOrNull())
            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())
            val isPublicBody = isPublic.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val capturedAtBody = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                .format(java.util.Date()).toRequestBody("text/plain".toMediaTypeOrNull())

            repository.uploadMoment(
                dailyLogIdBody,
                imagePart,
                captionBody,
                isPublicBody,
                capturedAtBody
            ).onSuccess {
                fetchMyMoments()
            }
            _isLoading.value = false
        }
    }
}
