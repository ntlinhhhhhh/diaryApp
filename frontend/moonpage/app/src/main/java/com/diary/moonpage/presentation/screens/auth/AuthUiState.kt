package com.diary.moonpage.presentation.screens.auth

data class AuthUiState (
    val emailInput: String = "",
    val usernameInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val otpCodeInput: String = "",

    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val otpCodeError: String? = null,

    val isLoading: Boolean = false,
    val generalError: String? = null

)