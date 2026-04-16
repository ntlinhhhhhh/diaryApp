package com.diary.moonpage.data.remote.dto.auth

data class ResetPasswordRequestDTO (
    val email: String,
    val otpCode: String,
    val newPassword: String
)