package com.diary.moonpage.domain.usecase

import android.util.Patterns
import com.diary.moonpage.data.remote.dto.auth.ForgotPasswordRequestDTO
import com.diary.moonpage.domain.repository.AuthRepository
import jakarta.inject.Inject


class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid Email format"))
        }
        val request = ForgotPasswordRequestDTO(email)
        return repository.forgotPassword(request)
    }

}