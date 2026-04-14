package com.diary.moonpage.presentation.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.theme.*

@Composable
fun ProfileHeader(title: String, onNotificationClick: () -> Unit, onSettingsClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) MoonLightText else MoonDarkText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Đẩy title ra giữa

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 22.sp
            ),
            modifier = Modifier.padding(start = 48.dp) // Cân bằng với 2 icon bên phải
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onNotificationClick) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = textColor)
        }
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = textColor)
        }
    }
}

@Composable
fun UserInfoCard(name: String, userId: String, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MoonDarkSurface else Color.White
    val textColor = if (isDark) MoonLightText else MoonDarkText

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isDark) MoonDarkInputBackground else MoonInputBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Face, contentDescription = "Avatar", tint = textColor, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = textColor)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Outlined.Security, contentDescription = "Verified", tint = MoonBrownButton, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "ID $userId",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor.copy(alpha = 0.6f))
                )
            }

            Icon(Icons.Outlined.ChevronRight, contentDescription = "Detail", tint = textColor.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    val isDark = isSystemInDarkTheme()
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = if (isDark) MoonLightText else MoonDarkText
        ),
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MoonDarkSurface else Color.White
    val textColor = if (isDark) MoonLightText else MoonDarkText

    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(color = textColor.copy(alpha = 0.6f)))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = textColor))
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) MoonDarkSurface else Color.White
    val textColor = if (isDark) MoonLightText else MoonDarkText

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = textColor))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .padding(4.dp)
                        .background(MoonBrownButton.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MoonBrownButton)
                }
            }


            Icon(Icons.Outlined.ChevronRight, contentDescription = "Go", tint = textColor.copy(alpha = 0.4f))
        }
    }
}