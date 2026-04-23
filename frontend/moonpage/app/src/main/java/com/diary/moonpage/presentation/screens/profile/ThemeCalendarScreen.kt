package com.diary.moonpage.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.components.profile.CalendarBottomActions
import com.diary.moonpage.presentation.components.profile.MonthGridView
import com.diary.moonpage.presentation.components.profile.YearHeader
import com.diary.moonpage.presentation.theme.MoonPageTheme

/**
 * Stateful Screen for Theme Calendar
 */
@Composable
fun ThemeCalendarScreen(
    onNavigateBack: () -> Unit
) {
    // These states would ideally come from a ViewModel
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val selectedYear = "2026"
    val userName = "🥑"

    ThemeCalendarContent(
        year = selectedYear,
        userName = userName,
        months = months,
        onNavigateBack = onNavigateBack,
        onHelpClick = { /* Handle help action */ },
        onPreviousYearClick = { /* Change to previous year */ },
        onNextYearClick = { /* Change to next year */ },
        onDownloadClick = { /* Handle download */ },
        onShareClick = { /* Handle share */ }
    )
}

/**
 * Stateless Content for Theme Calendar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeCalendarContent(
    year: String,
    userName: String,
    months: List<String>,
    onNavigateBack: () -> Unit,
    onHelpClick: () -> Unit,
    onPreviousYearClick: () -> Unit,
    onNextYearClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Theme Calendar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHelpClick) {
                        Icon(Icons.Rounded.HelpOutline, contentDescription = "Help", tint = colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorScheme.background)
            )
        },
        bottomBar = {
            Surface(
                color = colorScheme.background,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                CalendarBottomActions(
                    onDownloadClick = onDownloadClick,
                    onShareClick = onShareClick
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            YearHeader(
                year = year,
                userName = userName,
                onPreviousClick = onPreviousYearClick,
                onNextClick = onNextYearClick
            )

            MonthGridView(months = months)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeCalendarPreview() {
    MoonPageTheme {
        ThemeCalendarScreen { }
    }
}
