package com.diary.moonpage.data.remote.api

import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.LoginResponseDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDTO
    ): Response<LoginResponseDTO>

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDTO
    ): Response<RegisterResponseDto>
}