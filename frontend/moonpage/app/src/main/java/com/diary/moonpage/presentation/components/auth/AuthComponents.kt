package com.diary.moonpage.presentation.components.auth

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.R
import com.diary.moonpage.presentation.theme.*

@Composable
fun AuthHeader(title: String, subtitle: String) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText

    Text(
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displayLarge.copy(
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
        modifier = Modifier.padding(bottom = 32.dp)
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    trailingLabel: String? = null,
    placeholderText: String = "",
    iconVector: ImageVector? = null,
    iconRes: Int? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val inputBgColor = if (isDark) MoonDarkInputBackground else MoonInputBackground
    val linkColor = if (isDark) MoonDarkLinkColor else MoonLinkColor

    var passwordVisible by remember { mutableStateOf(false) }

    val hasTrailingIcon = isPassword || iconVector != null || iconRes != null

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        )
        if (trailingLabel != null && onTrailingClick != null) {
            Text(
                text = trailingLabel,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = linkColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable { onTrailingClick() }
                    .padding(vertical = 4.dp)
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        placeholder = {
            if (placeholderText.isNotEmpty()) {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor.copy(alpha = 0.4f)
                )
            }
        },

        trailingIcon = if (hasTrailingIcon) {
            {
                if (isPassword) {
                    val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = textColor.copy(alpha = 0.5f))
                    }
                }
                else if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                else if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else null,

        colors = TextFieldDefaults.colors(
            focusedContainerColor = inputBgColor,
            unfocusedContainerColor = inputBgColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = textColor
        ),
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor)
    )
}

@Composable
fun AuthDivider(text: String) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val lineColor = if (isDark) MoonLightText.copy(alpha = 0.2f) else MoonDarkText.copy(alpha = 0.1f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(1f), color = lineColor)
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider(modifier = Modifier.weight(1f), color = lineColor)
    }
}

@Composable
fun SocialLoginButton(text: String, iconResId: Int, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val cardBg = if (isDark) MoonDarkSurface else Color.White
    val borderColor = if (isDark) MoonLightText.copy(alpha = 0.2f) else MoonDarkText.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.05f),
                ambientColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
             Image(
                 painter = painterResource(id = iconResId),
                 contentDescription = "$text Icon",
                 modifier = Modifier.size(24.dp)
             )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AuthFooter(questionText: String, actionText: String, onActionClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val linkColor = if (isDark) MoonDarkLinkColor else MoonPrimaryButtonColor


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = questionText,
            style = MaterialTheme.typography.labelLarge,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = actionText,
            style = MaterialTheme.typography.labelLarge.copy(
                color = linkColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .clickable { onActionClick() }
                .padding(4.dp)
        )
    }
}

@Composable
fun TopCircularIcon() {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) MoonDarkSurface else Color.White
    val iconColor = if (isDark) MoonDarkLinkColor else MoonPrimaryButtonColor


    Box(
        modifier = Modifier
            .size(72.dp)
            .shadow(12.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.08f))
            .background(bgColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_reset_password),
            contentDescription = "Reset Icon",
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun OtpInputField(otpText: String, onOtpTextChange: (String) -> Unit, otpCount: Int = 6) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) MoonDarkInputBackground else MoonInputBackground
    val textColor = if (isDark) MoonLightText else MoonDarkText

    BasicTextField(
        value = otpText,
        onValueChange = { if (it.length <= otpCount) onOtpTextChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                repeat(otpCount) { index ->
                    val char = when {
                        index >= otpText.length -> ""
                        else -> "•"
                    }
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(46.dp)

                            .background(bgColor, RoundedCornerShape(18.dp))
                            .border(1.dp, textColor, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.titleLarge.copy(color = textColor, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AuthHeaderPreviewLight() {
    MoonPageTheme {
        AuthHeader("Welcome back", "Continue your journey of self-reflection.")
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthHeaderPreviewDark() {
    MoonPageTheme {
        AuthHeader("Welcome back", "Continue your journey of self-reflection.")
    }
}