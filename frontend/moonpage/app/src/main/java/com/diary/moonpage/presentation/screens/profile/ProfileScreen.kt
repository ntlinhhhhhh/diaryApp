package com.diary.moonpage.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.presentation.components.profile.ActionCard
import com.diary.moonpage.presentation.components.profile.Card
import com.diary.moonpage.presentation.components.profile.ProfileHeader
import com.diary.moonpage.presentation.components.profile.SectionTitle
import com.diary.moonpage.presentation.components.profile.StatCard
import com.diary.moonpage.presentation.components.profile.UserInfoCard
import com.diary.moonpage.presentation.theme.*

@Composable
fun ProfileScreen() {
    val isDark = isSystemInDarkTheme()
    val outerBgColor = MaterialTheme.colorScheme.background
    val innerBgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onSurface
    val inputBgColor = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        containerColor = outerBgColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Xử lý FAB */ },
                containerColor = inputBgColor,
                contentColor = textColor,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape, spotColor = inputBgColor)
            ) {
                Icon(Icons.Outlined.AddReaction, contentDescription = "Add Emotion")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(outerBgColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(innerBgColor)
                    .padding(horizontal = 14.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                ProfileHeader(
                    title = "My Info",
                    onNotificationClick = { /* TODO */ },
                    onSettingsClick = { /* TODO */ }
                )

                UserInfoCard(
                    name = "Alex Wanderer",
                    userId = "#0320",
                    onClick = { /* TODO */ }
                )

                SectionTitle("My Records")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard(
                        title = "Recorded days",
                        value = "128",
                        modifier = Modifier.weight(1f)
                    )

                    ActionCard(
                        title = "Photos",
                        icon = Icons.Outlined.PhotoLibrary,
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    title = "Theme Calendar",
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = { /* TODO */ }
                )

                SectionTitle("More")

                Card(
                    title = "Widgets",
                    icon = Icons.Outlined.Widgets,
                    onClick = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    title = "Invite a Friend",
                    icon = Icons.Outlined.PersonAdd,
                    onClick = { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MoonPageTheme {
        ProfileScreen()
    }
}