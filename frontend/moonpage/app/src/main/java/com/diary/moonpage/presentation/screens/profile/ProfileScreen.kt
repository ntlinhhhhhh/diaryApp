package com.diary.moonpage.presentation.screens.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.presentation.components.profile.*
import com.diary.moonpage.presentation.components.core.layout.SectionTitle
import com.diary.moonpage.presentation.theme.*

/**
 * Stateful Component for Profile Screen
 */
@Composable
fun ProfileScreen(
    onNavigateToAccount: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPhotos: () -> Unit,
    onNavigateToThemeCalendar: () -> Unit,
    onNavigateToWidgets: () -> Unit,
    onNavigateToInviteFriend: () -> Unit
) {
    ProfileScreenContent(
        userId = "#0320",
        recordedDays = "8",
        photoCount = "3",
        onNotificationClick = onNavigateToNotifications,
        onSettingsClick = onNavigateToSettings,
        onAccountClick = onNavigateToAccount,
        onPhotosClick = onNavigateToPhotos,
        onThemeCalendarClick = onNavigateToThemeCalendar,
        onWidgetsClick = onNavigateToWidgets,
        onInviteFriendClick = onNavigateToInviteFriend
    )
}

/**
 * Stateless Content for Profile Screen
 */
@Composable
fun ProfileScreenContent(
    userId: String,
    recordedDays: String,
    photoCount: String,
    onNotificationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onPhotosClick: () -> Unit,
    onThemeCalendarClick: () -> Unit,
    onWidgetsClick: () -> Unit,
    onInviteFriendClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            ProfileHeader(
                title = "My Info",
                onNotificationClick = onNotificationClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 2. Account Section
            SectionTitle("Account")
            UserInfoCard(
                userId = userId,
                onClick = onAccountClick
            )

            // 3. My Records Section
            SectionTitle("My records")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Recorded days",
                    value = recordedDays,
                    modifier = Modifier.weight(1f)
                )

                ActionCard(
                    title = "Photos",
                    value = photoCount,
                    modifier = Modifier.weight(1f),
                    onClick = onPhotosClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuItem(
                title = "Theme Calendar",
                icon = Icons.Rounded.CalendarMonth,
                onClick = onThemeCalendarClick
            )

            // 4. More Section
            SectionTitle("More")

            ProfileMenuItem(
                title = "Widgets",
                icon = Icons.Rounded.Widgets,
                onClick = onWidgetsClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileMenuItem(
                title = "Invite a Friend",
                icon = Icons.Rounded.PersonAdd,
                onClick = onInviteFriendClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MoonPageTheme {
        ProfileScreenContent(
            userId = "#0320",
            recordedDays = "8",
            photoCount = "3",
            onNotificationClick = {},
            onSettingsClick = {},
            onAccountClick = {},
            onPhotosClick = {},
            onThemeCalendarClick = {},
            onWidgetsClick = {},
            onInviteFriendClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenDarkPreview() {
    MoonPageTheme {
        ProfileScreenContent(
            userId = "#0320",
            recordedDays = "8",
            photoCount = "3",
            onNotificationClick = {},
            onSettingsClick = {},
            onAccountClick = {},
            onPhotosClick = {},
            onThemeCalendarClick = {},
            onWidgetsClick = {},
            onInviteFriendClick = {}
        )
    }
}