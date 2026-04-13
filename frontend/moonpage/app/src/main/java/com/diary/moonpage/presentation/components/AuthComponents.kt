package com.diary.moonpage.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.theme.*

// 1. HEADER
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    trailingLabel: String? = null,
    onTrailingClick: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText
    val inputBgColor = if (isDark) MoonDarkSurface else MoonInputBackground

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
                    color = MoonLinkColor,
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = inputBgColor,
            unfocusedContainerColor = inputBgColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = textColor
        ),
        shape = RoundedCornerShape(25.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
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

// 5. FOOTER
@Composable
fun AuthFooter(questionText: String, actionText: String, onActionClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText

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
                color = MoonSecondary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .clickable { onActionClick() }
                .padding(4.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AuthHeaderPreviewLight() {
    MoonPageTheme {
        AuthHeader("Welcome back", "Continue your journey of self-reflection.")
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthHeaderPreviewDark() {
    MoonPageTheme {
        AuthHeader("Welcome back", "Continue your journey of self-reflection.")
    }
}