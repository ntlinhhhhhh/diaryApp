package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.AuthApi
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.LoginResponseDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterResponseDto
import com.diary.moonpage.domain.model.User
import com.diary.moonpage.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun login(request: LoginRequestDTO): Result<User> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.toUser()
                Result.success(user)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
            }
        }

    override suspend fun register(request: RegisterRequestDTO): Result<User> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.toUser()
                Result.success(user)
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}