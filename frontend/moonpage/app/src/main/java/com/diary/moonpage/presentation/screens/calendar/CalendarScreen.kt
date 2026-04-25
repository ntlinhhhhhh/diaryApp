package com.diary.moonpage.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.data.remote.api.DailyLogResponse
import com.diary.moonpage.presentation.components.calendar.*
import com.diary.moonpage.presentation.theme.MoonPageTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek

// Mood helper data
private data class MoodVisual(val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

private fun moodVisualFor(baseMoodId: Int?): MoodVisual {
    return when (baseMoodId) {
        1 -> MoodVisual(Color(0xFFFFEB3B), Icons.Rounded.SentimentSatisfiedAlt, "Happy")
        2 -> MoodVisual(Color(0xFFAED581), Icons.Rounded.SentimentSatisfied, "Good")
        3 -> MoodVisual(Color(0xFF66BB6A), Icons.Rounded.SentimentNeutral, "Neutral")
        4 -> MoodVisual(Color(0xFF78909C), Icons.Rounded.SentimentVeryDissatisfied, "Sad")
        5 -> MoodVisual(Color(0xFF546E7A), Icons.Rounded.SentimentVeryDissatisfied, "Tired")
        else -> MoodVisual(Color(0xFFAED581), Icons.Rounded.SentimentSatisfied, "Good")
    }
}

@Composable
fun CalendarScreen(
    onNavigateToFilter: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDailyLog: (String) -> Unit,
    onNavigateToThemeCalendar: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()

    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val currentMonthName = currentYearMonth.format(monthFormatter)

    // Bottom sheet state for day detail
    var showDayDetail by remember { mutableStateOf(false) }
    var detailDate by remember { mutableStateOf(LocalDate.now()) }

    // Month picker dialog
    var showMonthPicker by remember { mutableStateOf(false) }

    CalendarContent(
        currentMonthName = currentMonthName,
        selectedDate = selectedDate,
        currentYearMonth = currentYearMonth,
        dailyLogs = dailyLogs,
        onDateSelected = { date ->
            selectedDate = date
            if (dailyLogs[date] != null) {
                detailDate = date
                showDayDetail = true
            } else {
                onNavigateToDailyLog(date.toString())
            }
        },
        onFilterClick = onNavigateToFilter,
        onSettingsClick = onNavigateToSettings,
        onShareClick = { /* TODO */ },
        onThemeClick = onNavigateToThemeCalendar,
        onMonthClick = { showMonthPicker = true },
        onNavigateToDailyLog = onNavigateToDailyLog
    )

    // Day detail bottom sheet
    if (showDayDetail) {
        val log = dailyLogs[detailDate]
        if (log != null) {
            val mv = moodVisualFor(log.baseMoodId)
            DayDetailBottomSheet(
                date = detailDate,
                moodIcon = mv.icon,
                moodColor = mv.color,
                moodLabel = mv.label,
                noteSnippet = log.note,
                onDismiss = { showDayDetail = false },
                onEdit = {
                    showDayDetail = false
                    onNavigateToDailyLog(detailDate.toString())
                },
                onDelete = {
                    showDayDetail = false
                    // TODO: wire delete through viewModel
                },
                onShare = {
                    // TODO: share
                }
            )
        }
    }

    // Month picker dialog
    if (showMonthPicker) {
        MonthPickerDialog(
            currentYearMonth = currentYearMonth,
            onMonthSelected = { offset ->
                viewModel.changeMonth(offset)
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}

@Composable
fun CalendarContent(
    currentMonthName: String,
    selectedDate: LocalDate,
    currentYearMonth: YearMonth,
    dailyLogs: Map<LocalDate, DailyLogResponse>,
    onDateSelected: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onThemeClick: () -> Unit = {},
    onMonthClick: () -> Unit = {},
    onNavigateToDailyLog: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToDailyLog(LocalDate.now().toString()) },
                containerColor = Color(0xFF4CAF50),
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Log", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CalendarTopBar(
                onFilterClick = onFilterClick,
                onSettingsClick = onSettingsClick,
                onThemeClick = onThemeClick
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
                Spacer(modifier = Modifier.size(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMonthClick() }
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
                verticalArrangement = Arrangement.Top
            ) {
                CalendarHeader()

                val daysInMonth = (1..currentYearMonth.lengthOfMonth()).toList()
                val firstDayOfMonth = currentYearMonth.atDay(1)
                val firstDayOffset = if (firstDayOfMonth.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value
                val today = LocalDate.now()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    items(firstDayOffset) {
                        DayItem(day = null, isSelected = false, moodColor = null, onClick = {})
                    }

                    items(daysInMonth) { day ->
                        val date = currentYearMonth.atDay(day)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val logForDay = dailyLogs[date]

                        val mv = if (logForDay != null) moodVisualFor(logForDay.baseMoodId) else null

                        DayItem(
                            day = day,
                            isSelected = isSelected,
                            moodColor = mv?.color,
                            moodIcon = mv?.icon,
                            isToday = isToday,
                            onClick = { onDateSelected(date) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Simple month picker dialog: shows prev/next month navigation and a 12-month grid for current year.
 */
@Composable
fun MonthPickerDialog(
    currentYearMonth: YearMonth,
    onMonthSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onMonthSelected(-12L) }) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "Prev Year")
                }
                Text(
                    text = currentYearMonth.year.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onMonthSelected(12L) }) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "Next Year")
                }
            }
        },
        text = {
            Column {
                months.chunked(3).forEachIndexed { rowIdx, rowMonths ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowMonths.forEachIndexed { colIdx, month ->
                            val monthNumber = rowIdx * 3 + colIdx + 1
                            val isCurrentMonth = monthNumber == currentYearMonth.monthValue
                            val offset = (monthNumber - currentYearMonth.monthValue).toLong()
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onMonthSelected(offset) },
                                color = if (isCurrentMonth) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = month,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = if (isCurrentMonth) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
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
