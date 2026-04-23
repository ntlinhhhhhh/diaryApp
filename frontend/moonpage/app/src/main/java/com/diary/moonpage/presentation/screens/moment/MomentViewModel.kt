package com.diary.moonpage.presentation.screens.moment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.domain.repository.MomentRepository
import com.diary.moonpage.presentation.components.moment.MomentTag
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MomentViewModel @Inject constructor(
    private val repository: MomentRepository
) : ViewModel() {

    private val _moments = MutableStateFlow<List<MomentResponse>>(emptyList())
    val moments: StateFlow<List<MomentResponse>> = _moments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val allTags = listOf(
        MomentTag("text", Icons.Rounded.TextFields, "Message"),
        MomentTag("review", Icons.Rounded.Star, "Review"),
        MomentTag("location", Icons.Rounded.LocationOn, "Location"),
        MomentTag("weather", Icons.Rounded.WbSunny, "Weather"),
        MomentTag("time", Icons.Rounded.AccessTime, SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())),
        MomentTag("party", null, "Party Time!", containerColor = Color(0xFF80FFE8), contentColor = Color.Black),
        MomentTag("ootd", null, "OOTD", containerColor = Color.White, contentColor = Color.Black),
        MomentTag("missyou", null, "Miss you", containerColor = Color(0xFFFF4B4B), contentColor = Color.White)
    )

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
        dailyLogId: String = "default_log_id",
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
