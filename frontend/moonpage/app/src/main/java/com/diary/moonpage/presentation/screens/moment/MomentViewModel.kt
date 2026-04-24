package com.diary.moonpage.presentation.screens.moment

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.domain.repository.MomentRepository
import com.diary.moonpage.presentation.components.moment.MomentTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private val Context.momentDataStore by preferencesDataStore(name = "moment_cache")

@HiltViewModel
class MomentViewModel @Inject constructor(
    private val repository: MomentRepository,
    private val gson: Gson,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _moments = MutableStateFlow<List<MomentResponse>>(emptyList())
    val moments: StateFlow<List<MomentResponse>> = _moments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _localPaths = MutableStateFlow<Map<String, String>>(emptyMap())
    val localPaths: StateFlow<Map<String, String>> = _localPaths.asStateFlow()

    private val MOMENT_LIST_KEY = stringPreferencesKey("cached_moments")

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
        loadCachedMoments()
        fetchMyMoments()
    }

    private fun loadCachedMoments() {
        viewModelScope.launch {
            try {
                val preferences = context.momentDataStore.data.first()
                val json = preferences[MOMENT_LIST_KEY]
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<MomentResponse>>() {}.type
                    val cachedList: List<MomentResponse> = gson.fromJson(json, type)
                    _moments.value = cachedList
                }
            } catch (e: Exception) {
                Log.e("MomentVM", "Error loading cache", e)
            }
        }
    }

    private fun saveMomentsToCache(list: List<MomentResponse>) {
        viewModelScope.launch {
            try {
                val json = gson.toJson(list)
                context.momentDataStore.edit { prefs ->
                    prefs[MOMENT_LIST_KEY] = json
                }
            } catch (e: Exception) {
                Log.e("MomentVM", "Error saving cache", e)
            }
        }
    }

    fun fetchMyMoments(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_moments.value.isEmpty()) _isLoading.value = true
            
            repository.getMyMoments().onSuccess { newList ->
                _moments.value = newList
                saveMomentsToCache(newList)
            }.onFailure {
                Log.e("MomentVM", "Fetch moments failed", it)
            }
            _isLoading.value = false
        }
    }

    fun uploadMoment(
        imageFile: File,
        caption: String,
        location: String? = null,
        weather: String? = null,
        rating: Float? = null,
        dailyLogId: String = "default_log_id",
        isPublic: Boolean = true,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imagePart = MultipartBody.Part.createFormData(
                    "imageFile",
                    imageFile.name,
                    imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                
                val dailyLogIdBody = dailyLogId.toRequestBody("text/plain".toMediaTypeOrNull())
                val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())
                val isPublicBody = isPublic.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val capturedAtStr = sdf.format(Date())
                val capturedAtBody = capturedAtStr.toRequestBody("text/plain".toMediaTypeOrNull())

                val locationBody = location?.toRequestBody("text/plain".toMediaTypeOrNull())
                val weatherBody = weather?.toRequestBody("text/plain".toMediaTypeOrNull())
                val ratingBody = rating?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                repository.uploadMoment(
                    dailyLogIdBody,
                    imagePart,
                    captionBody,
                    isPublicBody,
                    capturedAtBody,
                    locationBody,
                    weatherBody,
                    ratingBody
                ).onSuccess { response ->
                    // Lưu local để load nhanh
                    val fileName = "moment_${response.id}.jpg"
                    val permanentFile = File(context.filesDir, "moments/$fileName")
                    permanentFile.parentFile?.mkdirs()
                    imageFile.copyTo(permanentFile, overwrite = true)
                    
                    _localPaths.update { it + (response.imageUrl to permanentFile.absolutePath) }

                    val updatedList = (listOf(response) + _moments.value).distinctBy { it.id }
                    _moments.value = updatedList
                    
                    onSuccess()
                    fetchMyMoments(forceRefresh = true)
                }.onFailure {
                    Log.e("MomentVM", "Upload failed", it)
                }
            } catch (e: Exception) {
                Log.e("MomentVM", "Error preparing upload", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
