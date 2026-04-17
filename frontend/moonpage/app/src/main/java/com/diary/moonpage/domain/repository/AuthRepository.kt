package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.dto.auth.ForgotPasswordRequestDTO
import com.diary.moonpage.data.remote.dto.auth.GoogleLoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.ResetPasswordRequestDTO
import com.diary.moonpage.data.remote.dto.auth.VerifyOtpRequestDTO
import com.diary.moonpage.data.remote.dto.auth.VerifyOtpResponseDTO
import com.diary.moonpage.domain.model.User

interface AuthRepository {
    suspend fun login(request: LoginRequestDTO): Result<User>;
    suspend fun register(request: RegisterRequestDTO): Result<User>;
    suspend fun googleLogin(request: GoogleLoginRequestDTO): Result<User>;
    suspend fun forgotPassword(request: ForgotPasswordRequestDTO): Result<Unit>;
    suspend fun verifyOtp(request: VerifyOtpRequestDTO): Result<VerifyOtpResponseDTO>;
    suspend fun resetPassword(request: ResetPasswordRequestDTO): Result<Unit>;
}