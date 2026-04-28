package com.diary.moonpage.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.data.remote.api.DailyLogResponse
import com.diary.moonpage.presentation.components.calendar.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

import com.diary.moonpage.core.util.MoonIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onNavigateToFilter: () -> Unit = {}, // Vẫn giữ nhưng sẽ dùng BottomSheet
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
    var showMonthPicker by remember { mutableStateOf(false) }

    // Thêm state để hiện Filter Screen
    var showFilterSheet by remember { mutableStateOf(false) }

    val monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    val currentMonthName = currentYearMonth.format(monthFormatter)

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
        onFilterClick = { showFilterSheet = true }, // Mở Filter BottomSheet
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

    // Hiển thị Filter Screen dưới dạng Bottom Sheet
    if (showFilterSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Color.Transparent, // Đã có Surface trong FilterScreen.kt
            dragHandle = null
        ) {
            FilterScreen(
                onDismiss = { showFilterSheet = false },
                onSeeResults = {
                    showFilterSheet = false
                    // TODO: Gọi ViewModel để áp dụng Filter
                }
            )
        }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CalendarTopBar(
                    onFilterClick = onFilterClick,
                    onSettingsClick = onSettingsClick,
                    onThemeClick = onThemeClick
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                    val targetOffset = java.time.temporal.ChronoUnit.MONTHS.between(
                        baseYearMonth,
                        currentYearMonth
                    ).toInt()
                    val targetPage = initialPage + targetOffset
                    if (pagerState.currentPage != targetPage) {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    verticalAlignment = Alignment.Top
                ) { page ->
                    val offset = page - initialPage
                    val pageYearMonth = baseYearMonth.plusMonths(offset.toLong())
                    val daysInMonth = pageYearMonth.lengthOfMonth()
                    val firstDayOfMonth = pageYearMonth.atDay(1)
                    val firstDayOffset = if (firstDayOfMonth.dayOfWeek == java.time.DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value
                    val today = LocalDate.now()

                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val totalCells = firstDayOffset + daysInMonth
                        val rows = (totalCells + 6) / 7

                        for (rowIndex in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                for (colIndex in 0 until 7) {
                                    val cellIndex = rowIndex * 7 + colIndex
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cellIndex in firstDayOffset until totalCells) {
                                            val day = cellIndex - firstDayOffset + 1
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
                                        } else {
                                            DayItem(day = null, isSelected = false, moodColor = null, onClick = {})
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val selectedLog = dailyLogs[selectedDate]
                if (selectedLog != null) {
                    val mv = moodVisualFor(selectedLog.baseMoodId)
                    val activityNames = selectedLog.activityIds?.mapNotNull { id ->
                        dynamicActivities.find { it.id == id }?.name
                    } ?: emptyList()

                    // --- DI CHUYỂN CÁC ICON RA NGOÀI CARD ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp), // Nằm ngay trên Card
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Eco, // Mầm cây
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(28.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onShareClick) {
                                Icon(Icons.Rounded.IosShare, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(22.dp))
                            }
                            IconButton(onClick = { onEditLog(selectedDate) }) {
                                Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(22.dp))
                            }
                            IconButton(onClick = { onDeleteLog(selectedDate) }) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                    // ----------------------------------------

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        DayDetailArea(
                            date = selectedDate,
                            moodIcon = mv.icon,
                            moodDrawable = mv.drawableRes,
                            moodColor = mv.color,
                            moodLabel = mv.label,
                            noteSnippet = selectedLog.note,
                            activityNames = activityNames
                        )
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 52.dp)
            ) { data ->
                val isError = data.visuals.message.contains("future", ignoreCase = true) ||
                        data.visuals.message.contains("Failed", ignoreCase = true)
                val isSuccess = data.visuals.message.contains("success", ignoreCase = true) ||
                        data.visuals.message.contains("deleted", ignoreCase = true) ||
                        data.visuals.message.contains("recorded", ignoreCase = true) ||
                        data.visuals.message.contains("updated", ignoreCase = true) ||
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
    activityNames: List<String> = emptyList()
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Đã xóa phần chứa 4 icons ở đây để chuyển lên trên Card

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), // Thu hẹp padding dọc một chút
            verticalAlignment = Alignment.Top
        ) {
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

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(120.dp)
                    .background(cs.onSurface.copy(alpha = 0.05f))
            )

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

// Giữ nguyên phần MonthYearPickerBottomSheet và MonthYearWheelColumn...
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
                    MonthYearWheelColumn(
                        items = monthNames,
                        initialIndex = selectedMonthIndex,
                        onIndexChange = { selectedMonthIndex = it },
                        modifier = Modifier.width(96.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
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