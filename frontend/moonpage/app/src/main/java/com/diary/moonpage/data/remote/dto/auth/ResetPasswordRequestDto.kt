package com.diary.moonpage.data.remote.dto.auth

data class ResetPasswordRequestDTO (
    val email: String,
    val resetToken: String,
    val newPassword: String
)