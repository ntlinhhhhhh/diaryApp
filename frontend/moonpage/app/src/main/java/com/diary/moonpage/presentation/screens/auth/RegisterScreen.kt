package com.diary.moonpage.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.auth.SocialLoginButton
import com.diary.moonpage.presentation.components.auth.SocialLoginButton
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonTextField
import com.diary.moonpage.presentation.components.core.layout.MoonDivider
import com.diary.moonpage.presentation.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToLoginGoogle: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUpClick = viewModel::register,
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToLoginGoogle = onNavigateToLoginGoogle,
        onRegisterSuccess = onRegisterSuccess
    )
}

@Composable
fun RegisterScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToLoginGoogle: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val screenBgColor = MaterialTheme.colorScheme.background
    val backIconColor = MaterialTheme.colorScheme.onSurface
    val cardBgColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.RegisterSuccess -> {
                    onRegisterSuccess()
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = backIconColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AuthHeader(
                            title = "Your Space",
                            subtitle = "Create a sacred space for your\nthoughts and emotions today."
                        )

                        MoonTextField(
                            value = uiState.usernameInput ?: "",
                            onValueChange = onUsernameChange,
                            placeholderText = "Enter your username",
                            label = "Username",
                            iconVector = Icons.Outlined.Person,
                            errorText = uiState.usernameError
                        )

                        MoonTextField(
                            value = uiState.emailInput,
                            onValueChange = onEmailChange,
                            label = "Email address",
                            placeholderText = "Enter your email",
                            iconVector = Icons.Outlined.Email,
                            errorText = uiState.emailError
                        )

                        MoonTextField(
                            value = uiState.passwordInput,
                            onValueChange = onPasswordChange,
                            label = "Password",
                            placeholderText = "Enter your password",
                            isPassword = true,
                            errorText = uiState.passwordError
                        )

                        MoonTextField(
                            value = uiState.confirmPasswordInput,
                            onValueChange = onConfirmPasswordChange,
                            label = "Confirm Password",
                            placeholderText = "Confirm your password",
                            isPassword = true,
                            errorText = uiState.confirmPasswordError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MoonPrimaryButton(
                            text = "Sign Up",
                            enabled = !uiState.isLoading,
                            onClick = {
                                keyboardController?.hide()
                                onSignUpClick()
                            },
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        MoonDivider(text = "OR SIGN UP WITH")

                        Spacer(modifier = Modifier.height(32.dp))

                        SocialLoginButton(
                            text = "Sign up with Google",
                            iconResId = R.drawable.ic_google,
                            onClick = { onNavigateToLoginGoogle() }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AuthFooter(
                            questionText = "Already have an account? ",
                            actionText = "Login here",
                            onActionClick = onNavigateToLogin
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreviewLight() {
    MoonPageTheme {
        RegisterScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onSignUpClick = {},
            onNavigateBack = {},
            onNavigateToLogin = {},
            onNavigateToLoginGoogle = {},
            onRegisterSuccess = {},
            onConfirmPasswordChange = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterScreenPreviewDark() {
    MoonPageTheme {
        RegisterScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onSignUpClick = {},
            onNavigateBack = {},
            onNavigateToLogin = {},
            onNavigateToLoginGoogle = {},
            onRegisterSuccess = {},
            onConfirmPasswordChange = {}
        )
    }
}