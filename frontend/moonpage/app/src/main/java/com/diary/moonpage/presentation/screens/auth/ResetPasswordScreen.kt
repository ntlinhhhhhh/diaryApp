package com.diary.moonpage.presentation.screens.auth

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.auth.OtpInputField
import com.diary.moonpage.presentation.components.auth.TopCircularIcon
import com.diary.moonpage.presentation.components.landing.MoonPrimaryButton
import com.diary.moonpage.presentation.theme.*



@Composable
fun ResetPasswordScreen(
    onNavigateToLogin: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val isDark = isSystemInDarkTheme()
    val screenBgColor = if (isDark) MoonDarkBackground else MoonCreamBackground
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val cardBgColor = if (isDark) MoonDarkSurface else Color.White
    val inputBgColor = if (isDark) MoonDarkInputBackground else MoonInputBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
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
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.08f)),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "VERIFICATION CODE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
                OtpInputField(otpText = otpCode, onOtpTextChange = { otpCode = it })

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "NEW PASSWORD",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("••••••••", color = textColor.copy(alpha = 0.4f)) },
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(if (showNewPassword) Icons.Outlined.Lock else Icons.Outlined.Lock, contentDescription = null, tint = textColor.copy(alpha = 0.5f))
                        }
                    },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = textColor
                    ),
                    shape = RoundedCornerShape(25.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONFIRM PASSWORD",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = textColor.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("••••••••", color = textColor.copy(alpha = 0.4f)) },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(if (showConfirmPassword) Icons.Outlined.Lock else Icons.Outlined.Lock, contentDescription = null, tint = textColor.copy(alpha = 0.5f))
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = textColor
                    ),
                    shape = RoundedCornerShape(25.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                MoonPrimaryButton(
                    text = "Reset Password",
                    onClick = onNavigateToLogin,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthFooter(
            questionText = "Didn't receive a code? ",
            actionText = "Didn't receive a code? ",
            onActionClick = {}
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPreview() {
    MoonPageTheme { ResetPasswordScreen({}) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResetPasswordDarkPreview() {
    MoonPageTheme { ResetPasswordScreen({}) }
}