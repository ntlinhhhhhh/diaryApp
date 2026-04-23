package com.diary.moonpage.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.data.remote.dto.auth.UpdateProfileRequestDto
import com.diary.moonpage.data.remote.dto.auth.UserResponseDto
import com.diary.moonpage.domain.model.Theme
import com.diary.moonpage.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserResponseDto? = null,
    val myThemes: List<Theme> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false, // Separate loading state for updates
    val error: String? = null
)

sealed class ProfileUiEvent {
    data class ShowSnackBar(val message: String) : ProfileUiEvent()

    object UpdateSuccess : ProfileUiEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadProfile()
        loadMyThemes()
    }

    fun loadProfile() {
        viewModelScope.launch {
            // Chỉ hiện loading chính nếu chưa có dữ liệu user
            _uiState.update { it.copy(isLoading = it.user == null) }
            userRepository.getCurrentUser()
                .onSuccess { userDto ->
                    _uiState.update { it.copy(user = userDto, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun loadMyThemes() {
        viewModelScope.launch {
            userRepository.getMyThemes()
                .onSuccess { themes ->
                    _uiState.update { it.copy(myThemes = themes) }
                }
        }
    }

    fun updateProfile(name: String, gender: String?, birthday: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            val request = UpdateProfileRequestDto(name = name, gender = gender, birthday = birthday)
            userRepository.updateProfile(request)
                .onSuccess { updatedUser ->
                    // Gộp dữ liệu mới với dữ liệu hiện tại để tránh mất thông tin (flicker)
                    _uiState.update { state ->
                        val currentUser = state.user
                        val mergedUser = currentUser?.copy(
                            name = if (!updatedUser.name.isNullOrBlank()) updatedUser.name else name,
                            gender = updatedUser.gender ?: gender ?: currentUser.gender,
                            birthday = updatedUser.birthday ?: birthday ?: currentUser.birthday,
                            avatarUrl = updatedUser.avatarUrl ?: currentUser.avatarUrl
                        ) ?: updatedUser
                        
                        state.copy(user = mergedUser, isUpdating = false)
                    }
                    
                    _uiEvent.emit(ProfileUiEvent.UpdateSuccess)
                    _uiEvent.emit(ProfileUiEvent.ShowSnackBar("Profile updated successfully"))
                    
                    // Đồng bộ lại với server một lần nữa để chắc chắn
                    loadProfile()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isUpdating = false) }
                    _uiEvent.emit(ProfileUiEvent.ShowSnackBar(e.message ?: "Update failed"))
                }
        }
    }

    fun deleteAccount(id: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteUser(id)
                .onSuccess {
                    onDeleted()
                }
                .onFailure { e ->
                    _uiEvent.emit(ProfileUiEvent.ShowSnackBar(e.message ?: "Delete failed"))
                }
        }
    }
}
