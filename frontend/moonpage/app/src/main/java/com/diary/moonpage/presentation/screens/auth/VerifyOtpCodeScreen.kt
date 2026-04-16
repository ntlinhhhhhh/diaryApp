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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.components.auth.AuthFooter
import com.diary.moonpage.presentation.components.auth.AuthHeader
import com.diary.moonpage.presentation.components.core.buttons.MoonPrimaryButton
import com.diary.moonpage.presentation.components.core.inputs.MoonOtpField
import com.diary.moonpage.presentation.components.core.navigation.TopCircularIcon
import com.diary.moonpage.presentation.theme.*

@Composable
fun VerifyOtpScreen(
    onNavigateBack: () -> Unit,
    onVerifySubmit: (String) -> Unit,
    onResendClick: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val screenBgColor = MaterialTheme.colorScheme.background
    val cardBgColor = MaterialTheme.colorScheme.surface
    val iconColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = onNavigateBack, // Gọn hơn, không cần cặp ngoặc nhọn { } nếu chỉ gọi hàm
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
                    otpText = otpCode,
                    onOtpTextChange = { otpCode = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                MoonPrimaryButton(
                    text = "Verify",
                    // THAY ĐỔI: Gọi hàm submit và truyền mã OTP người dùng vừa nhập ra bên ngoài
                    onClick = { onVerifySubmit(otpCode) },
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
}

@Preview(showBackground = true)
@Composable
fun VerifyOtpPreview() {
    MoonPageTheme {
        VerifyOtpScreen(
            onNavigateBack = {},
            onVerifySubmit = {}, // Cập nhật Preview
            onResendClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VerifyOtpDarkPreview() {
    MoonPageTheme {
        VerifyOtpScreen(
            onNavigateBack = {},
            onVerifySubmit = {}, // Cập nhật Preview
            onResendClick = {}
        )
    }
}