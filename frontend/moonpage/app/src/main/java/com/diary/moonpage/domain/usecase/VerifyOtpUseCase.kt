package com.diary.moonpage.domain.usecase

import com.diary.moonpage.data.remote.dto.auth.VerifyOtpRequestDTO
import com.diary.moonpage.data.remote.dto.auth.VerifyOtpResponseDTO
import com.diary.moonpage.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String): Result<VerifyOtpResponseDTO> {
        if (otpCode.isBlank()) {
            return Result.failure(Exception("OTP code cannot be empty."))
        }

        val request = VerifyOtpRequestDTO(email, otpCode)
        return repository.verifyOtp(request)
    }
}