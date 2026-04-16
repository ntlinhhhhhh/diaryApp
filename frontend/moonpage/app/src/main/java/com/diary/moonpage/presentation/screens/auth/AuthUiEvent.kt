package com.diary.moonpage.presentation.screens.auth

sealed class AuthUiEvent {
    data class LoginSuccess(val token: String): AuthUiEvent()
    data class RegisterSuccess(val message: String): AuthUiEvent()
    data class ForgotPasswordSuccess(val message: String): AuthUiEvent()
    data class ResetPasswordSuccess(val message: String): AuthUiEvent()
    data class ShowSnackBar(val message: String): AuthUiEvent()
    data class NavigateToVerifyOtp(val email: String): AuthUiEvent()
    data class NavigateToResetPassword(val email: String, val otpCode: String): AuthUiEvent()
    object NavigateToLogin: AuthUiEvent()
    object NavigateToRegister: AuthUiEvent()
}