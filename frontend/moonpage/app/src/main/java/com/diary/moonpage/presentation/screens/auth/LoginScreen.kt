package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.*
import com.diary.moonpage.presentation.theme.*

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("user@gmail.com") }
    var password by remember { mutableStateOf("........") }
    val scrollState = rememberScrollState()

    val isDark = isSystemInDarkTheme()
    val screenBgColor = if (isDark) MoonDarkBackground else MoonCreamBackground
    val backIconColor = if (isDark) MoonLightText else MoonDarkText
    val cardBgColor = if (isDark) MoonDarkSurface else Color.White

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(vertical = 20.dp)
            .verticalScroll(scrollState),
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
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
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

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address"
                )

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    isPassword = true,
                    trailingLabel = "Forgot Password?",
                    onTrailingClick = onNavigateToForgotPassword
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoonPrimaryButton(
                    text = "Login",
                    onClick = onLoginSuccess
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthDivider(text = "OR CONTINUE WITH")

                Spacer(modifier = Modifier.height(32.dp))

                SocialLoginButton(
                    text = "Login with Google",
                    iconResId = R.drawable.ic_google,
                    onClick = { }
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
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreviewLight() {
    MoonPageTheme {
        LoginScreen(
            onNavigateBack = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onLoginSuccess = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreviewDark() {
    MoonPageTheme {
        LoginScreen(
            onNavigateBack = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onLoginSuccess = {}
        )
    }
}