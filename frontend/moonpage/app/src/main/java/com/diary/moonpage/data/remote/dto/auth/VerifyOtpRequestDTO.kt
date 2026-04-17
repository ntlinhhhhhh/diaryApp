package com.diary.moonpage.data.remote.dto.auth

data class VerifyOtpRequestDTO (
    val email: String,
    val otpCode: String
)