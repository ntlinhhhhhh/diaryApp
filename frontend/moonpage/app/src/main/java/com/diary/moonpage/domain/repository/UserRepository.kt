package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.dto.auth.UpdateProfileRequestDto
import com.diary.moonpage.data.remote.dto.auth.UserResponseDto
import com.diary.moonpage.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): Result<UserResponseDto>
    suspend fun updateProfile(request: UpdateProfileRequestDto): Result<UserResponseDto>
    suspend fun getMyThemes(): Result<List<Theme>>
    suspend fun deleteUser(id: String): Result<Unit>
}
