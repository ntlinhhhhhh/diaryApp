package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.ThemeApi
import com.diary.moonpage.domain.model.Theme
import com.diary.moonpage.domain.repository.ThemeRepository
import com.google.gson.Gson
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val api: ThemeApi
) : ThemeRepository {

    override suspend fun getAllThemes(token: String): Result<List<Theme>> {
        return try {
            val response = api.getAllThemes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOwnedThemes(token: String): Result<List<Theme>> {
        return try {
            val response = api.getOwnedThemes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun buyTheme(token: String, themeId: String): Result<Unit> {
        return try {
            val response = api.buyTheme("Bearer $token", themeId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setActiveTheme(token: String, themeId: String): Result<Unit> {
        return try {
            val response = api.setActiveTheme("Bearer $token", themeId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = parseErrorResponse(response.errorBody()?.string())
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorResponse(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "An unknown error occurred"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
            val errorMap: Map<String, Any> = Gson().fromJson(errorBody, type)
            val message = errorMap["message"]
                ?: errorMap["error"]
                ?: errorMap["errors"]
            message?.toString() ?: errorBody
        } catch (e: Exception) {
            errorBody
        }
    }
}
