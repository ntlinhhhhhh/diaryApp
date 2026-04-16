package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.theme.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.auth.SocialLoginButton
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonTextField
import com.diary.moonpage.presentation.components.core.layout.MoonDivider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToLoginGoogle: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login,
        onNavigateBack = onNavigateBack,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
        onNavigateToLoginGoogle = onNavigateToLoginGoogle,
        onLoginSuccess = onLoginSuccess
    )
}

@Composable
fun LoginScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToLoginGoogle:() -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val screenBgColor = MaterialTheme.colorScheme.background
    val backIconColor = MaterialTheme.colorScheme.onSurface
    val cardBgColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.LoginSuccess -> {
                    onLoginSuccess(event.token)
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
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    },
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
                            title = "Welcome Back",
                            subtitle = "Continue your journey of self-\nreflection and mindful awareness."
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
                            isPassword = true,
                            trailingLabel = "Forgot Password?",
                            placeholderText = "Enter your password",
                            errorText = uiState.passwordError,
                            onTrailingClick = { onNavigateToForgotPassword() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MoonPrimaryButton(
                            text = "Login",
                            enabled = !uiState.isLoading,
                            onClick = {
                                keyboardController?.hide()
                                onLoginClick()
                            }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        MoonDivider(text = "OR CONTINUE WITH")

                        Spacer(modifier = Modifier.height(32.dp))

                        SocialLoginButton(
                            text = "Login with Google",
                            iconResId = R.drawable.ic_google,
                            onClick = { onNavigateToLoginGoogle() }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AuthFooter(
                            questionText = "Don't have an account? ",
                            actionText = "Sign up for free",
                            onActionClick = onNavigateToRegister
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
fun LoginScreenPreviewLight() {
    MoonPageTheme {
        LoginScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateBack = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onNavigateToLoginGoogle = {},
            onLoginSuccess = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreviewDark() {
    MoonPageTheme {
        LoginScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateBack = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onNavigateToLoginGoogle = {},
            onLoginSuccess = {}
        )
    }
}
