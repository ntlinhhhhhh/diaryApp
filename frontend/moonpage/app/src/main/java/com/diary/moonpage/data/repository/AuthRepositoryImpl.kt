package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.AuthApi
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.ErrorResponse
import com.diary.moonpage.domain.model.User
import com.diary.moonpage.domain.repository.AuthRepository
import com.google.gson.Gson
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
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
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
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "An unknow error occurred"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
            val errorMap: Map<String, Any> = Gson().fromJson(errorBody, type)
            val message = errorMap["message"]
                ?: errorMap["measge"]
                ?: errorMap["error"]
                ?: errorMap["errors"]

            message?.toString() ?: errorBody
        } catch (e: Exception) {
            errorBody ?: "An unknown error occurred"
        }
    }
}