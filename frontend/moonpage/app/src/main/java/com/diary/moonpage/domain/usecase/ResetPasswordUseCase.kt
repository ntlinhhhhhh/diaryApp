package com.diary.moonpage.domain.usecase

import com.diary.moonpage.data.remote.dto.auth.ResetPasswordRequestDTO
import com.diary.moonpage.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private  val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String, newPassword: String): Result<Unit> {
        if (newPassword.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters."))
        }

        val request = ResetPasswordRequestDTO(email, otpCode, newPassword)
        return repository.resetPassword(request)
    }
}