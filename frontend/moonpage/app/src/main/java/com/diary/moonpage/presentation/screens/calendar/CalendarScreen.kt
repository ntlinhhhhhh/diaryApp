package com.diary.moonpage.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.SentimentSatisfiedAlt
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.diary.moonpage.presentation.components.calendar.*
import com.diary.moonpage.presentation.theme.MoonPageTheme
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    navController: androidx.navigation.NavController? = null,
    onNavigateToFilter: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    CalendarContent(
        currentMonthName = "Apr 2026",
        selectedDate = selectedDate,
        onDateSelected = { selectedDate = it },
        onFilterClick = onNavigateToFilter,
        onShareClick = { /* TODO */ },
        onNavigateToDailyLog = { dateStr ->
            navController?.navigate("daily_log_screen/$dateStr")
        }
    )
}

@Composable
fun CalendarContent(
    currentMonthName: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
    onShareClick: () -> Unit,
    onNavigateToDailyLog: (String) -> Unit
) {
    val themePrimary = MaterialTheme.colorScheme.primary
    val themeSecondary = MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CalendarTopBar(
            onFilterClick = onFilterClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Month Year header & Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(24.dp)) // padding to balance center

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* Show month picker */ }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentMonthName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Rounded.IosShare,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Top // Changed to Top to keep it clean after the 20dp spacer
        ) {
            CalendarHeader()

            // Calendar Grid Placeholder
            val daysInMonth = (1..30).toList()
            val firstDayOffset = 3 // Wednesday for Apr 2026

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                // Empty spaces for offset
                items(firstDayOffset) {
                    DayItem(day = null, isSelected = false, moodColor = null, onClick = {})
                }

                items(daysInMonth) { day ->
                    val isSelected = day == 14

                    // Sử dụng màu từ theme thay vì màu xanh fix cứng
                    val moodColor = when (day) {
                        6, 14, 20 -> Color(0xFFAED581) // Light green
                        8, 23 -> Color(0xFF66BB6A) // Medium green
                        12 -> Color(0xFF78909C) // Grey
                        else -> null
                    }
                    val moodIcon = when (day) {
                        6, 14, 20 -> Icons.Rounded.SentimentSatisfied
                        8, 23 -> Icons.Rounded.SentimentNeutral
                        12 -> Icons.Rounded.SentimentVeryDissatisfied
                        else -> null
                    }

                    DayItem(
                        day = day,
                        isSelected = isSelected,
                        moodColor = moodColor,
                        moodIcon = moodIcon,
                        onClick = {
                            onDateSelected(LocalDate.of(2026, 4, day))
                            onNavigateToDailyLog("2026-04-$day")
                        }
                    )
                }
            }
        }

        DiaryEntryPreview(
            date = "14 Tue",
            moodIcon = Icons.Rounded.SentimentSatisfiedAlt,
            moodColor = themePrimary
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    MoonPageTheme(darkTheme = false) {
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenDarkPreview() {
    MoonPageTheme(darkTheme = true) {
    }
}
