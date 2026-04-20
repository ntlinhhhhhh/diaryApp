package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonTextField
import com.diary.moonpage.presentation.components.core.navigation.TopCircularIcon
import com.diary.moonpage.presentation.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Stateful Component (Smart Component)
 * Handles ViewModel, state observation, and event collection.
 */
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToReset: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ForgotPasswordScreenContent(
        uiState = uiState,
        uiEvent = viewModel.uiEvent,
        onEmailChange = viewModel::onEmailChange,
        onSendOtpClick = {
            // Logic to trigger OTP sending
            // viewModel.sendOtp()
        },
        onNavigateBack = onNavigateBack,
        onNavigateToReset = onNavigateToReset
    )
}

/**
 * Stateless Component (Dumb Component)
 * Purely presentational. Doesn't know about ViewModel or API logic.
 */
@Composable
fun ForgotPasswordScreenContent(
    uiState: AuthUiState,
    uiEvent: Flow<AuthUiEvent>,
    onEmailChange: (String) -> Unit,
    onSendOtpClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToReset: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val screenBgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBgColor = MaterialTheme.colorScheme.surface
    val iconColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.ForgotPasswordSuccess -> {
                    // Navigate to reset screen with email for verification
                    onNavigateToReset(uiState.emailInput)
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
            Spacer(modifier = Modifier.height(18.dp))

            // Custom Top Bar with Back Button
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

            TopCircularIcon()

            Spacer(modifier = Modifier.height(24.dp))

            AuthHeader(
                title = "Forgot Password",
                subtitle = "Please enter your registered email\naddress to receive a secure 6-digit OTP code."
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
                    MoonTextField(
                        value = uiState.emailInput,
                        onValueChange = onEmailChange,
                        label = "Email Address",
                        placeholderText = "name@example.com",
                        iconVector = Icons.Outlined.Email,
                        errorText = uiState.emailError
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MoonPrimaryButton(
                        text = "Send OTP",
                        enabled = !uiState.isLoading,
                        onClick = {
                            keyboardController?.hide()
                            onSendOtpClick()
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AuthFooter(
                questionText = "Suddenly remembered? ",
                actionText = "Sign In",
                onActionClick = onNavigateBack
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Footer Security Note
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.width(40.dp), color = textColor.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Outlined.Lock, 
                    contentDescription = "Secure", 
                    modifier = Modifier.size(14.dp), 
                    tint = textColor.copy(alpha = 0.3f)
                )
                Text(
                    text = " SECURE SANCTUARY ",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = textColor.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HorizontalDivider(modifier = Modifier.width(40.dp), color = textColor.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Loading Overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    MoonPageTheme {
        ForgotPasswordScreenContent(
            uiState = AuthUiState(),
            uiEvent = MutableSharedFlow<AuthUiEvent>().asSharedFlow(),
            onEmailChange = {},
            onSendOtpClick = {},
            onNavigateBack = {},
            onNavigateToReset = {}
        )
    }
}
