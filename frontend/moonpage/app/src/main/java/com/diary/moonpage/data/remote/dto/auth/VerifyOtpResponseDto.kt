package com.diary.moonpage.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponseDTO(
    @SerializedName("resetToken")
    val resetToken: String
)