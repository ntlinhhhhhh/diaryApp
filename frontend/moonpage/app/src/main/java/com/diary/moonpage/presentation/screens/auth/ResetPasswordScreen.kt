package com.diary.moonpage.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonTextField
import com.diary.moonpage.presentation.components.core.inputs.OtpInputField
import com.diary.moonpage.presentation.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Stateful Component (Smart Component)
 * Manages state from ViewModel and handles navigation/events.
 */
@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ResetPasswordScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onOtpCodeChange = viewModel::onOtpCodeChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onResetClick = {
            // Need to implement reset password logic in ViewModel if not exists
            // viewModel.resetPassword()
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

/**
 * Stateless Component (Dumb Component)
 * Purely UI, receives data via parameters and notifies parent via callbacks.
 * Uses MaterialTheme.colorScheme for consistent theming.
 */
@Composable
fun ResetPasswordScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onOtpCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Using theme colors instead of hardcoded values
    val screenBgColor = MaterialTheme.colorScheme.background
    val cardBgColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.ResetPasswordSuccess -> {
                    onNavigateToLogin()
                }
                is AuthUiEvent.ShowSnackBar -> {
                    launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = screenBgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            TopCircularIcon()

            Spacer(modifier = Modifier.height(24.dp))

            AuthHeader(
                title = "Reset Your Sanctuary",
                subtitle = "Enter the 6-digit code and choose\na new password."
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp, 
                        shape = RoundedCornerShape(24.dp), 
                        spotColor = Color.Black.copy(alpha = 0.08f) // Shadow remains mostly black but subtle
                    ),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "VERIFICATION CODE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold, 
                            color = onSurfaceColor.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    OtpInputField(
                        otpText = uiState.otpCodeInput, 
                        onOtpTextChange = onOtpCodeChange 
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MoonTextField(
                        value = uiState.passwordInput,
                        onValueChange = onPasswordChange,
                        label = "NEW PASSWORD",
                        placeholderText = "••••••••",
                        isPassword = true,
                        errorText = uiState.passwordError
                    )

                    MoonTextField(
                        value = uiState.confirmPasswordInput,
                        onValueChange = onConfirmPasswordChange,
                        label = "CONFIRM PASSWORD",
                        placeholderText = "••••••••",
                        isPassword = true,
                        errorText = uiState.confirmPasswordError
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    MoonPrimaryButton(
                        text = "Reset Password",
                        enabled = !uiState.isLoading,
                        onClick = {
                            keyboardController?.hide()
                            onResetClick()
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthFooter(
                questionText = "Didn't receive a code? ",
                actionText = "Resend OTP",
                onActionClick = { /* Handle resend logic via parent callback if needed */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TopCircularIcon() {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPreview() {
    MoonPageTheme {
        ResetPasswordScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onOtpCodeChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResetPasswordDarkPreview() {
    MoonPageTheme {
        ResetPasswordScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onOtpCodeChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetClick = {},
            onNavigateToLogin = {}
        )
    }
}
