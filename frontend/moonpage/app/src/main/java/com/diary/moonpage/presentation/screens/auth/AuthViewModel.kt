package com.diary.moonpage.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.domain.model.User
import com.diary.moonpage.domain.usecase.ForgotPasswordUseCase
import com.diary.moonpage.domain.usecase.GoogleLoginUseCase
import com.diary.moonpage.domain.usecase.LoginUseCase
import com.diary.moonpage.domain.usecase.RegisterUserCase
import com.diary.moonpage.domain.usecase.ResetPasswordUseCase
import com.diary.moonpage.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUserCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    private val _uiEvent = Channel<AuthUiEvent>()

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(emailInput = email)}

    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(usernameInput = username) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordInput = password) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPasswordInput = confirmPassword)}
    }

    fun onOtpCodeChange(otpCode: String) {
        _uiState.update { it.copy(otpCodeInput = otpCode) }
    }

    fun login() {
        viewModelScope.launch {
            println("--- BẤM NÚT LOGIN ---")
            _uiState.update { it.copy(emailError = null, passwordError = null) }
            val email = uiState.value.emailInput
            val password = uiState.value.passwordInput
            var hasError = false

            if (email.isBlank()) {
                _uiState.update { it.copy(emailError = "Email cannot be empty.") }
                hasError = true
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.update { it.copy(emailError = "Invalid email format.") }
                hasError = true
                return@launch
            }

            if (password.isBlank()) {
                _uiState.update { it.copy(passwordError = "Password cannot be empty.") }
                hasError = true
                return@launch
            }

            if (hasError) return@launch

            _uiState.update { it.copy(isLoading = true) }
            try {
                val loginRequest = LoginRequestDTO(email, password)
                val result: Result<User> = loginUseCase(loginRequest)

                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.LoginSuccess(user.token))
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleAuthError(exception.message)
//                    _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Login failed. Please check your credentials."))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(e.message ?: "Connection error. Please check your internet connection."))
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            val state = uiState.value
            var hasError = false

            _uiState.update {
                it.copy(
                    emailError = null,
                    usernameError = null,
                    passwordError = null,
                    confirmPasswordError = null
                )
            }

            if (state.usernameInput.isBlank()) {
                _uiState.update { it.copy(usernameError = "Username cannot be empty.") }
                hasError = true
            }

            if (state.emailInput.isBlank()) {
                _uiState.update { it.copy(emailError = "Email cannot be empty.") }
                hasError = true
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.emailInput).matches()) {
                _uiState.update { it.copy(emailError = "Invalid email format.") }
                hasError = true
            }

            if (state.passwordInput.isBlank()) {
                _uiState.update { it.copy(passwordError = "Password cannot be empty.") }
                hasError = true
            } else if (state.passwordInput.length < 6) {
                _uiState.update { it.copy(passwordError = "Password must be at least 6 characters.") }
                hasError = true
            }

            if (state.confirmPasswordInput.isBlank()) {
                _uiState.update { it.copy(confirmPasswordError = "Please confirm your password.") }
                hasError = true
            } else if (state.passwordInput != state.confirmPasswordInput) {
                _uiState.update { it.copy(confirmPasswordError = "Passwords do not match.") }
                hasError = true
            }

            if (hasError) return@launch

            if (state.passwordInput.length < 6) {
                _uiEvent.send(AuthUiEvent.ShowSnackBar("Password must be at least 6 characters."))
                return@launch
            }

            if (state.passwordInput != state.confirmPasswordInput) {
                _uiEvent.send(AuthUiEvent.ShowSnackBar("Passwords do not match."))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            try {
                val request = RegisterRequestDTO(
                    email = state.emailInput,
                    name = state.usernameInput,
                    password = state.passwordInput
                )
                val result: Result<User> = registerUseCase(request)

                result.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.RegisterSuccess("Registration successful. Please log in."))
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleAuthError(exception.message)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(e.message ?: "Connection error. Please check your internet connection."))
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = googleLoginUseCase(idToken)

            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.LoginSuccess(user.token))
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Google login failed."))
            }
        }
    }

    fun forgotPassword() {
        val email = uiState.value.emailInput.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = forgotPasswordUseCase(email)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, savedEmailForOtp = email) }
                _uiEvent.send(AuthUiEvent.NavigateToVerifyOtp(email))
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Failed to send OTP."))
            }
        }
    }

    fun verifyOtp() {
        val email = uiState.value.savedEmailForOtp
        val otpCode = uiState.value.otpCodeInput.trim()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = verifyOtpUseCase(email, otpCode)


            result.onSuccess { resetToken ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resetToken = resetToken
                    )
                }
                println("--- VERIFY OTP THÀNH CÔNG! TOKEN NHẬN ĐƯỢC LÀ: [$resetToken] ---")

                _uiEvent.send(AuthUiEvent.NavigateToResetPassword(email, resetToken))

            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Invalid OTP Code."))
            }
        }
    }

    fun resetPassword() {
        val email = uiState.value.savedEmailForOtp
        val token = uiState.value.resetToken
        val newPassword = uiState.value.passwordInput

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = resetPasswordUseCase(email, token, newPassword)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar("Password reset successful! Please login."))
                _uiEvent.send(AuthUiEvent.NavigateToLogin)
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Failed to reset password."))
            }
        }
    }

    // handle error
    private fun handleAuthError(message: String?) {
        val error = message ?: "An unknown error occurred"

        _uiState.update { it.copy(emailError = null, passwordError = null, usernameError = null, confirmPasswordError = null) }
        when {
            error.contains("Email", ignoreCase = true) || error.contains("User", ignoreCase = true) -> {
                _uiState.update { it.copy(emailError = error) }
            }
            error.contains("Password", ignoreCase = true) -> {
                _uiState.update { it.copy(passwordError = error) }
            }
            error.contains("Username", ignoreCase = true) || error.contains("Name", ignoreCase = true) -> {
                _uiState.update { it.copy(usernameError = error) }
            }
            else -> {
                viewModelScope.launch {
                    _uiEvent.send(AuthUiEvent.ShowSnackBar(error))
                }
            }
        }
    }
}