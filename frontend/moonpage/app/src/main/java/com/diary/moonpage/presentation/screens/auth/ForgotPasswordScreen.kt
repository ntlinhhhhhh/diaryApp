package com.diary.moonpage.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.navigation.TopCircularIcon
import com.diary.moonpage.presentation.theme.*



@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToReset: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val isDark = isSystemInDarkTheme()
    val screenBgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBgColor = MaterialTheme.colorScheme.surface
    val inputBgColor = MaterialTheme.colorScheme.surfaceVariant
    val iconColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = { onNavigateBack() },
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
                Text(
                    text = "Email Address",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = textColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@example.com", color = textColor.copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, contentDescription = "Email", tint = textColor.copy(alpha = 0.5f))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = textColor
                    ),
                    shape = RoundedCornerShape(25.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                MoonPrimaryButton(
                    text = "Send OTP",
                    onClick = { onNavigateToReset },
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthFooter(
            questionText = "Suddenly remembered? ",
            actionText = "Sign In",
            onActionClick = {}
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Divider(modifier = Modifier.width(40.dp), color = textColor.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Outlined.Lock, contentDescription = "Secure", modifier = Modifier.size(14.dp), tint = textColor.copy(alpha = 0.3f))
            Text(
                text = " SECURE SANCTUARY ",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = textColor.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Divider(modifier = Modifier.width(40.dp), color = textColor.copy(alpha = 0.1f))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    MoonPageTheme { ForgotPasswordScreen({}, {}, {}) }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForgotPasswordDarkModePreview() {
    MoonPageTheme { ForgotPasswordScreen({}, {}, {}) }
}
