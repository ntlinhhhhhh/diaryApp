package com.diary.moonpage.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonTextField
import com.diary.moonpage.presentation.components.core.navigation.TopCircularIcon
import com.diary.moonpage.presentation.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ResetPasswordScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onResetClick = viewModel::resetPassword,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun ResetPasswordScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val screenBgColor = MaterialTheme.colorScheme.background
    val cardBgColor = MaterialTheme.colorScheme.surface

    // Lắng nghe sự kiện từ ViewModel
    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.NavigateToLogin -> {
                    onNavigateToLogin() // ViewModel báo đổi pass thành công -> Chuyển về trang Login
                }
                is AuthUiEvent.ShowSnackBar -> {
                    launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                TopCircularIcon()

                Spacer(modifier = Modifier.height(24.dp))

                AuthHeader(
                    title = "Reset Your Sanctuary",
                    subtitle = "Create a new password for your journal."
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.08f)),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Nhập Pass mới
                        MoonTextField(
                            value = uiState.passwordInput,
                            onValueChange = onPasswordChange,
                            label = "New Password",
                            placeholderText = "••••••••",
                            isPassword = true,
                            errorText = uiState.passwordError,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        // Xác nhận Pass mới
                        MoonTextField(
                            value = uiState.confirmPasswordInput,
                            onValueChange = onConfirmPasswordChange,
                            label = "Confirm Password",
                            placeholderText = "••••••••",
                            isPassword = true,
                            errorText = uiState.confirmPasswordError,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onResetClick()
                                }
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Nút Gọi API
                        MoonPrimaryButton(
                            text = "Reset Password",
                            enabled = !uiState.isLoading, // Khóa nút khi đang gửi API
                            onClick = {
                                keyboardController?.hide() // Cất bàn phím
                                onResetClick()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Hiệu ứng Loading che phủ
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

// --- PHẦN PREVIEW ĐỂ TEST GIAO DIỆN ---
@Preview(showBackground = true)
@Composable
fun ResetPasswordPreviewLight() {
    MoonPageTheme {
        ResetPasswordScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResetPasswordPreviewDark() {
    MoonPageTheme {
        ResetPasswordScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onResetClick = {},
            onNavigateToLogin = {}
        )
    }
}
