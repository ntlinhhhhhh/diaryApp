package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthDivider
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.auth.AuthTextField
import com.diary.moonpage.presentation.components.auth.SocialLoginButton
import com.diary.moonpage.presentation.components.landing.MoonPrimaryButton
import com.diary.moonpage.presentation.theme.*

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val isDark = isSystemInDarkTheme()
    val screenBgColor = MaterialTheme.colorScheme.background
    val backIconColor = MaterialTheme.colorScheme.onSurface
    val cardBgColor = MaterialTheme.colorScheme.surface
    Column(
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
                    title = "Your Space",
                    subtitle = "Create a sacred space for your\nthoughts and emotions today."
                )

                AuthTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholderText = "Enter your username",
                    label = "Username",
                    iconVector = Icons.Outlined.Person
                )

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address",
                    placeholderText = "Enter your email",
                    iconVector = Icons.Outlined.Email,
                )

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholderText = "Enter your password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoonPrimaryButton(
                    text = "Sign Up",
                    onClick = onRegisterSuccess,
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthDivider(text = "OR SIGN UP WITH")

                Spacer(modifier = Modifier.height(32.dp))

                SocialLoginButton(
                    text = "Sign up with Google",
                    iconResId = R.drawable.ic_google,
                    onClick = { }
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
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreviewLight() {
    MoonPageTheme {
        RegisterScreen(
            onNavigateBack = {},
            onNavigateToLogin = {},
            onRegisterSuccess = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterScreenPreviewDark() {
    MoonPageTheme {
        RegisterScreen(
            onNavigateBack = {},
            onNavigateToLogin = {},
            onRegisterSuccess = {}
        )
    }
}