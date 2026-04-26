package com.diary.moonpage.presentation.screens.auth

import com.diary.moonpage.core.util.UiText

sealed class AuthUiEvent {
    data class LoginSuccess(val token: String, val userId: String, val isNewUser: Boolean = false): AuthUiEvent()
    data class RegisterSuccess(val message: String): AuthUiEvent()
    data class ForgotPasswordSuccess(val message: String): AuthUiEvent()
    data class ResetPasswordSuccess(val message: String): AuthUiEvent()
    data class ShowSnackBar(val message: UiText): AuthUiEvent()
    data class NavigateToVerifyOtp(val email: String): AuthUiEvent()
    data class NavigateToResetPassword(val email: String, val token: String): AuthUiEvent()
    object NavigateToLogin: AuthUiEvent()
    object NavigateToRegister: AuthUiEvent()
}