package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.dto.auth.ForgotPasswordRequestDTO
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.LoginResponseDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterResponseDto
import com.diary.moonpage.domain.model.User
import retrofit2.Response

interface AuthRepository {
    suspend fun login(request: LoginRequestDTO): Result<User>;
    suspend fun register(request: RegisterRequestDTO): Result<User>;
//    suspend fun forgotPassword(request: ForgotPasswordRequestDTO);
}