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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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

import com.diary.moonpage.core.util.MoonIcons
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

// Mood helper data
private data class MoodVisual(val color: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null, val drawableRes: Int? = null, val label: String)

private fun moodVisualFor(baseMoodId: Int?): MoodVisual {
    val moodIcon = when (baseMoodId) {
        1 -> MoonIcons.Moods.Happy
        2 -> MoonIcons.Moods.Good
        3 -> MoonIcons.Moods.Neutral
        4 -> MoonIcons.Moods.Sad
        5 -> MoonIcons.Moods.Angry
        else -> MoonIcons.Moods.Good
    }
    return MoodVisual(moodIcon.color, moodIcon.vector, moodIcon.drawableRes, moodIcon.name)
}

@Composable
fun CalendarScreen(
    createdLogDate: String? = null,
    onLogDateHandled: () -> Unit = {},
    logSavedMessage: String? = null,
    onMessageShown: () -> Unit = {},
    onNavigateToFilter: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDailyLog: (String) -> Unit,
    onNavigateToThemeCalendar: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val dailyLogs by viewModel.dailyLogs.collectAsState()
    val dynamicActivities by viewModel.dynamicActivities.collectAsState()

    var externalErrorMessage by remember { mutableStateOf<String?>(null) }

    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val currentMonthName = currentYearMonth.format(monthFormatter)

    // Month picker dialog
    var showMonthPicker by remember { mutableStateOf(false) }

    LaunchedEffect(createdLogDate) {
        if (createdLogDate != null) {
            viewModel.refreshLogs()
            selectedDate = LocalDate.parse(createdLogDate)
            onLogDateHandled()
        }
    }

    CalendarContent(
        currentMonthName = currentMonthName,
        selectedDate = selectedDate,
        currentYearMonth = currentYearMonth,
        dailyLogs = dailyLogs,
        dynamicActivities = dynamicActivities,
        onDateSelected = { date ->
            if (date.isAfter(LocalDate.now())) {
                externalErrorMessage = "You cannot record logs for future dates!"
            } else {
                selectedDate = date
                if (dailyLogs[date] == null) {
                    onNavigateToDailyLog(date.toString())
                }
            }
        },
        onFilterClick = onNavigateToFilter,
        onSettingsClick = onNavigateToSettings,
        onShareClick = { /* TODO */ },
        onThemeClick = onNavigateToThemeCalendar,
        onMonthClick = { showMonthPicker = true },
        onMonthChanged = { newMonth -> viewModel.setYearMonth(newMonth.year, newMonth.monthValue) },
        onNavigateToDailyLog = onNavigateToDailyLog,
        logSavedMessage = logSavedMessage ?: externalErrorMessage,
        onMessageShown = {
            onMessageShown()
            externalErrorMessage = null
        },
        onEditLog = { date ->
            onNavigateToDailyLog(date.toString())
        },
        onDeleteLog = { date ->
            viewModel.deleteDailyLog(
                date = date.toString(),
                onSuccess = { externalErrorMessage = "Record deleted successfully!" },
                onFailure = { error -> externalErrorMessage = error }
            )
        }
    )

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
    dynamicActivities: List<com.diary.moonpage.domain.model.Activity>,
    onDateSelected: (LocalDate) -> Unit,
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShareClick: () -> Unit,
    onThemeClick: () -> Unit = {},
    onMonthClick: () -> Unit = {},
    onMonthChanged: (YearMonth) -> Unit = {},
    onNavigateToDailyLog: (String) -> Unit,
    logSavedMessage: String? = null,
    onMessageShown: () -> Unit = {},
    onEditLog: (LocalDate) -> Unit = {},
    onDeleteLog: (LocalDate) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(logSavedMessage) {
        if (!logSavedMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(logSavedMessage)
            onMessageShown()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToDailyLog(LocalDate.now().toString()) },
                containerColor = MoonIcons.Moods.getMoodColor(1),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = MoonIcons.Moods.Good.drawableRes!!),
                    contentDescription = "New Log",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                CalendarTopBar(
                    onFilterClick = onFilterClick,
                    onSettingsClick = onSettingsClick,
                    onThemeClick = onThemeClick
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Month Year header
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
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
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.IosShare,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Top
                ) {
                    CalendarHeader()

                    val baseYearMonth = remember { currentYearMonth }
                    val initialPage = 500 * 12
                    val pagerState = rememberPagerState(
                        initialPage = initialPage,
                        pageCount = { initialPage * 2 }
                    )

                    LaunchedEffect(pagerState.currentPage) {
                        val offset = pagerState.currentPage - initialPage
                        val targetMonth = baseYearMonth.plusMonths(offset.toLong())
                        if (targetMonth != currentYearMonth) {
                            onMonthChanged(targetMonth)
                        }
                    }

                    LaunchedEffect(currentYearMonth) {
                        val targetOffset = java.time.temporal.ChronoUnit.MONTHS.between(baseYearMonth, currentYearMonth).toInt()
                        val targetPage = initialPage + targetOffset
                        if (pagerState.currentPage != targetPage) {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }

                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                        val offset = page - initialPage
                        val pageYearMonth = baseYearMonth.plusMonths(offset.toLong())
                        val daysInMonth = (1..pageYearMonth.lengthOfMonth()).toList()
                        val firstDayOfMonth = pageYearMonth.atDay(1)
                        val firstDayOffset = if (firstDayOfMonth.dayOfWeek == java.time.DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value
                        val today = LocalDate.now()

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(firstDayOffset) {
                                DayItem(day = null, isSelected = false, moodColor = null, onClick = {})
                            }

                            items(daysInMonth) { day ->
                                val date = pageYearMonth.atDay(day)
                                val isSelected = date == selectedDate
                                val isToday = date == today
                                val logForDay = dailyLogs[date]

                                val mv = if (logForDay != null) moodVisualFor(logForDay.baseMoodId) else null
                                val isLoggedToday = logForDay?.date == today.toString()

                                DayItem(
                                    day = day,
                                    isSelected = isSelected,
                                    moodColor = mv?.color,
                                    moodIcon = mv?.icon,
                                    moodDrawable = mv?.drawableRes,
                                    isToday = isToday,
                                    isDimmed = logForDay != null && !isLoggedToday,
                                    onClick = { onDateSelected(date) }
                                )
                            }
                        }
                    }
                }

                // Inline Day Detail Area
                val selectedLog = dailyLogs[selectedDate]
                if (selectedLog != null) {
                    val mv = moodVisualFor(selectedLog.baseMoodId)
                    val activityNames = selectedLog.activityIds?.mapNotNull { id ->
                        dynamicActivities.find { it.id == id }?.name
                    } ?: emptyList()

                    DayDetailArea(
                        date = selectedDate,
                        moodIcon = mv.icon,
                        moodDrawable = mv.drawableRes,
                        moodColor = mv.color,
                        moodLabel = mv.label,
                        noteSnippet = selectedLog.note,
                        activityNames = activityNames,
                        onEdit = { onEditLog(selectedDate) },
                        onDelete = { onDeleteLog(selectedDate) },
                        onShare = {}
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // SnackbarHost as overlay at top
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { data ->
                val isError = data.visuals.message.contains("future", ignoreCase = true) || 
                              data.visuals.message.contains("Failed", ignoreCase = true)
                val isSuccess = data.visuals.message.contains("success", ignoreCase = true) || 
                                data.visuals.message.contains("deleted", ignoreCase = true) ||
                                data.visuals.message.contains("recorded", ignoreCase = true) ||
                                data.visuals.message.contains("edited", ignoreCase = true)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF333333), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            isError -> Icons.Rounded.Error
                            isSuccess -> Icons.Rounded.CheckCircle
                            else -> Icons.Rounded.Info
                        }, 
                        contentDescription = null, 
                        tint = when {
                            isError -> Color(0xFFD32F2F)
                            isSuccess -> Color(0xFF4CAF50)
                            else -> Color(0xFFFFA000)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = data.visuals.message, 
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayDetailArea(
    date: LocalDate,
    moodIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    moodDrawable: Int? = null,
    moodColor: Color,
    moodLabel: String,
    noteSnippet: String?,
    activityNames: List<String> = emptyList(),
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Top row with Sprout icon and Action Icons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Eco,
                contentDescription = null,
                tint = Color(0xFF81C784),
                modifier = Modifier.size(24.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Rounded.IosShare, contentDescription = "Share", tint = cs.onSurface.copy(alpha = 0.35f), modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = cs.onSurface.copy(alpha = 0.35f), modifier = Modifier.size(22.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = cs.onSurface.copy(alpha = 0.35f), modifier = Modifier.size(22.dp))
                }
            }
        }

        // Main Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = cs.surface,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Left side: Mood and Date
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(moodColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (moodDrawable != null) {
                            Image(
                                painter = painterResource(id = moodDrawable),
                                contentDescription = null,
                                modifier = Modifier.size(42.dp)
                            )
                        } else if (moodIcon != null) {
                            Icon(
                                imageVector = moodIcon,
                                contentDescription = null,
                                tint = moodColor,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = cs.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${date.dayOfMonth} ${date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(1.dp)
                        .height(120.dp)
                        .background(cs.onSurface.copy(alpha = 0.05f))
                )

                // Right side: Activities Grid and Steps
                Column(modifier = Modifier.weight(1f)) {
                    if (activityNames.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            activityNames.forEach { name ->
                                val icon = com.diary.moonpage.core.util.MoonIcons.getIconForActivity(name)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(cs.onSurface.copy(alpha = 0.04f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (icon.drawableRes != null) {
                                        Image(
                                            painter = painterResource(id = icon.drawableRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    } else if (icon.vector != null) {
                                        Icon(
                                            imageVector = icon.vector,
                                            contentDescription = null,
                                            tint = icon.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (!noteSnippet.isNullOrBlank()) {
                        Text(
                            text = noteSnippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurface.copy(alpha = 0.7f),
                            lineHeight = 18.sp,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Steps Bar (Pill style)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = cs.onSurface.copy(alpha = 0.03f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.DirectionsWalk,
                                contentDescription = null,
                                tint = Color(0xFF7895CB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "3,546 steps",
                                style = MaterialTheme.typography.labelSmall,
                                color = cs.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
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
