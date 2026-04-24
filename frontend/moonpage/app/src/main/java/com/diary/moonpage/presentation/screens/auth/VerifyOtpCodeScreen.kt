package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonOtpField
import com.diary.moonpage.presentation.components.core.navigation.TopCircularIcon
import com.diary.moonpage.presentation.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Composable
fun VerifyOtpScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResetPassword: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    VerifyOtpScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onOtpCodeChange = viewModel::onOtpCodeChange,
        onVerifySubmit = viewModel::verifyOtp,
        onNavigateBack = onNavigateBack,
        onNavigateToResetPassword = onNavigateToResetPassword,
        onResendClick = viewModel::forgotPassword
    )
}

@Composable
fun VerifyOtpScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onOtpCodeChange: (String) -> Unit,
    onVerifySubmit: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToResetPassword: (String, String) -> Unit,
    onResendClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val screenBgColor = MaterialTheme.colorScheme.background
    val cardBgColor = MaterialTheme.colorScheme.surface
    val iconColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.NavigateToResetPassword -> {
                    onNavigateToResetPassword(event.email, event.token)
                }
                is AuthUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.message.asString(context))
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
                .verticalScroll(scrollState)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = iconColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TopCircularIcon()

            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = "Verify Account",
                subtitle = "Please enter the 6-digit code we sent\nto your email address."
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.08f)
                    ),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    MoonOtpField(
                        label = "Verification Code",
                        otpText = uiState.otpCodeInput,
                        onOtpTextChange = onOtpCodeChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MoonPrimaryButton(
                        text = "Verify",
                        enabled = !uiState.isLoading,
                        onClick = {
                            keyboardController?.hide()
                            onVerifySubmit()
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthFooter(
                questionText = "Didn't receive a code? ",
                actionText = "Resend",
                onActionClick = onResendClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyOtpPreview() {
    MoonPageTheme {
        VerifyOtpScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onOtpCodeChange = {},
            onVerifySubmit = {},
            onNavigateBack = {},
            onNavigateToResetPassword = { _, _ -> },
            onResendClick = {},
        )
    }
}