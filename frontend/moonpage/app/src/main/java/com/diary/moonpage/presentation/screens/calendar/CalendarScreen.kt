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
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Month picker bottom sheet
    if (showMonthPicker) {
        MonthYearPickerBottomSheet(
            currentYearMonth = currentYearMonth,
            onConfirm = { year, month ->
                viewModel.setYearMonth(year, month)
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
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Text("😊", fontSize = 24.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
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

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
 * Wheel-style BottomSheet for picking month + year, similar to DailyBean.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPickerBottomSheet(
    currentYearMonth: YearMonth,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    val years = (1990..2050).toList()
    val colorScheme = MaterialTheme.colorScheme

    var selectedMonthIndex by remember { mutableIntStateOf(currentYearMonth.monthValue - 1) }
    var selectedYearIndex by remember { mutableIntStateOf(years.indexOf(currentYearMonth.year).coerceAtLeast(0)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Select Month & Year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                // Selection indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month picker
                    MonthYearWheelColumn(
                        items = monthNames,
                        initialIndex = selectedMonthIndex,
                        onIndexChange = { selectedMonthIndex = it },
                        modifier = Modifier.width(96.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    // Year picker
                    MonthYearWheelColumn(
                        items = years.map { it.toString() },
                        initialIndex = selectedYearIndex,
                        onIndexChange = { selectedYearIndex = it },
                        circular = false,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    onConfirm(
                        years.getOrElse(selectedYearIndex) { currentYearMonth.year },
                        selectedMonthIndex + 1
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun MonthYearWheelColumn(
    items: List<String>,
    initialIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    circular: Boolean = true,
    itemHeight: androidx.compose.ui.unit.Dp = 48.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val count = items.size
    val MULT = 1000
    val startIndex = if (circular) MULT / 2 * count + initialIndex else initialIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)

    val selectedRealIndex by remember {
        derivedStateOf {
            if (circular) listState.firstVisibleItemIndex % count
            else listState.firstVisibleItemIndex.coerceIn(0, count - 1)
        }
    }
    LaunchedEffect(selectedRealIndex) { onIndexChange(selectedRealIndex) }

    LazyColumn(
        state = listState,
        flingBehavior = snapFling,
        modifier = modifier.height(itemHeight * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = itemHeight)
    ) {
        val itemCount = if (circular) MULT * count else count
        items(itemCount) { flatIndex ->
            val ri = if (circular) flatIndex % count else flatIndex
            val isSel = ri == selectedRealIndex
            Box(Modifier.height(itemHeight).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = items[ri],
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isSel) 18.sp else 13.sp,
                    color = if (isSel) colorScheme.onBackground else colorScheme.onBackground.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center
                )
            }
        }
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
