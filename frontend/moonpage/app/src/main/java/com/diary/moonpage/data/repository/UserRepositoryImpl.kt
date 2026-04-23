package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.UserApi
import com.diary.moonpage.data.remote.dto.auth.UpdateProfileRequestDto
import com.diary.moonpage.data.remote.dto.auth.UserResponseDto
import com.diary.moonpage.domain.model.Theme
import com.diary.moonpage.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun getCurrentUser(): Result<UserResponseDto> {
        return try {
            val response = userApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(request: UpdateProfileRequestDto): Result<UserResponseDto> {
        return try {
            val response = userApi.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyThemes(): Result<List<Theme>> {
        return try {
            val response = userApi.getMyThemes()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch themes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            val response = userApi.deleteUser(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Deletion failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
