package com.diary.moonpage.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.moonpage.core.util.TokenManager
import com.diary.moonpage.data.remote.dto.auth.LoginRequestDTO
import com.diary.moonpage.data.remote.dto.auth.RegisterRequestDTO
import com.diary.moonpage.data.remote.dto.auth.GoogleLoginRequestDTO
import com.diary.moonpage.domain.model.User
import com.diary.moonpage.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUserCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val tokenManager: TokenManager // Inject TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    private val _uiEvent = Channel<AuthUiEvent>()

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Flow để quan sát trạng thái đăng nhập từ bên ngoài
    val tokenFlow = tokenManager.getToken()

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
        _uiState.update { it.copy(otpCodeInput = otpCode, otpCodeError = null) }
        // Tự động verify khi đủ 6 số
        if (otpCode.length == 6) {
            verifyOtp()
        }
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
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.update { it.copy(emailError = "Invalid email format.") }
                hasError = true
            }

            if (password.isBlank()) {
                _uiState.update { it.copy(passwordError = "Password cannot be empty.") }
                hasError = true
            }

            if (hasError) return@launch

            _uiState.update { it.copy(isLoading = true) }
            try {
                val loginRequest = LoginRequestDTO(email, password)
                val result: Result<User> = loginUseCase(loginRequest)

                result.onSuccess { user ->
                    tokenManager.saveToken(user.token)
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.LoginSuccess(user.token))
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleAuthError(exception.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(e.message ?: "Connection error."))
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

            _uiState.update { it.copy(isLoading = true) }
            try {
                val request = RegisterRequestDTO(
                    email = state.emailInput,
                    name = state.usernameInput,
                    password = state.passwordInput
                )
                val result: Result<User> = registerUseCase(request)

                result.onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.RegisterSuccess("Registration successful. Please log in."))
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    handleAuthError(exception.message)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(e.message ?: "Connection error."))
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = googleLoginUseCase(idToken)

                result.onSuccess { user ->
                    tokenManager.saveToken(user.token)
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.LoginSuccess(user.token))
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AuthUiEvent.ShowSnackBar(exception.message ?: "Google login failed."))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _uiEvent.send(AuthUiEvent.ShowSnackBar(e.message ?: "Connection error."))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            _uiState.update { AuthUiState() }
        }
    }

    fun forgotPassword() {
        val email = uiState.value.emailInput.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = forgotPasswordUseCase(email)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, savedEmailForOtp = email) }
                _uiEvent.send(AuthUiEvent.NavigateToVerifyOtp(email))
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                handleAuthError(exception.message)
            }
        }
    }

    fun verifyOtp() {
        val email = uiState.value.savedEmailForOtp
        val otpCode = uiState.value.otpCodeInput.trim()

        if (otpCode.isBlank()) {
            _uiState.update { it.copy(otpCodeError = "OTP required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = verifyOtpUseCase(email, otpCode)

            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resetToken = response.resetToken
                    )
                }
                println("--- VERIFY OTP THÀNH CÔNG! TOKEN NHẬN ĐƯỢC LÀ: [${response.resetToken}] ---")
                _uiEvent.send(AuthUiEvent.NavigateToResetPassword(email, response.resetToken))
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                handleAuthError(exception.message)
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val email = uiState.value.savedEmailForOtp
            val resetToken = uiState.value.resetToken
            val newPassword = uiState.value.passwordInput

            if (newPassword.isBlank()) {
                _uiState.update { it.copy(passwordError = "New password required") }
                return@launch
            }
            if (newPassword != uiState.value.confirmPasswordInput) {
                _uiEvent.send(AuthUiEvent.ShowSnackBar("Passwords do not match"))
                return@launch
            }
            if (resetToken.isNullOrBlank()) {
                _uiEvent.send(AuthUiEvent.ShowSnackBar("Invalid or expired session. Please try again."))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            val result = resetPasswordUseCase(email, resetToken, newPassword)

            result.onSuccess {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        passwordInput = "",
                        confirmPasswordInput = "",
                        otpCodeInput = "",
                        resetToken = "",
                        emailInput = it.savedEmailForOtp // Giữ lại email để login
                    ) 
                }
                _uiEvent.send(AuthUiEvent.ResetPasswordSuccess("Password reset successful! Please login."))
                _uiEvent.send(AuthUiEvent.NavigateToLogin)
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                handleAuthError(exception.message)
            }
        }
    }

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
