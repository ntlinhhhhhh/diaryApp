package com.diary.moonpage.data.remote.dto.auth

data class VerifyOtpDTO (
    val email: String,
    val otpCode: String
)