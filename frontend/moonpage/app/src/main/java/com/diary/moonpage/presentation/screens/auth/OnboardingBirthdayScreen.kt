package com.diary.moonpage.presentation.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth

private val MONTHS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)
private val YEARS = (1950..2015).map { it.toString() }

// Large multiplier for infinite circular effect
private const val INFINITE_MULTIPLIER = 1000

@Composable
fun OnboardingBirthdayScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Default: 15th April 2000
    var selectedMonthIndex by remember { mutableIntStateOf(3) }   // April (0-based)
    var selectedDayIndex   by remember { mutableIntStateOf(14) }  // 15 (0-based)
    var selectedYearIndex  by remember { mutableIntStateOf(50) }  // 2000 (index in YEARS)

    // Auto-clamp days when month/year changes
    val daysInMonth by remember {
        derivedStateOf {
            val year = YEARS.getOrElse(selectedYearIndex) { "2000" }.toIntOrNull() ?: 2000
            val month = selectedMonthIndex + 1
            YearMonth.of(year, month).lengthOfMonth()
        }
    }
    // If current day exceeds days in selected month, clamp it
    LaunchedEffect(daysInMonth) {
        if (selectedDayIndex >= daysInMonth) {
            selectedDayIndex = daysInMonth - 1
        }
    }

    val progressAnim by animateFloatAsState(
        targetValue = 0.5f,
        animationSpec = tween(600),
        label = "progress"
    )

    Scaffold(containerColor = colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Top bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.onBackground
                    )
                }
                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .padding(horizontal = 8.dp),
                    color = colorScheme.primary,
                    trackColor = colorScheme.primary.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(48.dp)) // Căn phải thay cho nút Skip
            }

            // ── Content ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome! 🎉",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "When is your birthday?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "We'll recommend content for your age group\nand send you a gift! 🎁",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Emoji illustration
                Text(text = "🎂", fontSize = 80.sp)

                Spacer(modifier = Modifier.height(28.dp))

                // ── Date picker ───────────────────────────────────────────────
                val DAYS = remember(daysInMonth) { (1..daysInMonth).map { it.toString() } }

                Box(modifier = Modifier.fillMaxWidth()) {
                    // Center selection highlight
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                colorScheme.primary.copy(alpha = 0.08f),
                                RoundedCornerShape(12.dp)
                            )
                    )
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.Center).offset(y = (-24).dp),
                        color = colorScheme.primary.copy(alpha = 0.25f)
                    )
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.Center).offset(y = 24.dp),
                        color = colorScheme.primary.copy(alpha = 0.25f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Month – circular
                        CircularWheelColumn(
                            items = MONTHS,
                            initialIndex = selectedMonthIndex,
                            onIndexChange = { selectedMonthIndex = it },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Day – circular, respects daysInMonth
                        CircularWheelColumn(
                            items = DAYS,
                            initialIndex = selectedDayIndex.coerceAtMost(daysInMonth - 1),
                            onIndexChange = { selectedDayIndex = it },
                            modifier = Modifier.width(64.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Year – NOT circular, just scrollable
                        LinearWheelColumn(
                            items = YEARS,
                            initialIndex = selectedYearIndex,
                            onIndexChange = { selectedYearIndex = it },
                            modifier = Modifier.width(80.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Next Button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    val day   = (selectedDayIndex + 1).toString().padStart(2, '0')
                    val month = (selectedMonthIndex + 1).toString().padStart(2, '0')
                    val year  = YEARS[selectedYearIndex]
                    viewModel.setBirthday("$day/$month/$year")
                    onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    "Next",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// ── Circular Wheel (month, day) ──────────────────────────────────────────────

@Composable
fun CircularWheelColumn(
    items: List<String>,
    initialIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()
    val count = items.size

    // Start in the middle of the infinite list so we can scroll both directions
    val startIndex = INFINITE_MULTIPLIER / 2 * count + initialIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)

    val selectedRealIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex % count }
    }

    LaunchedEffect(selectedRealIndex) {
        onIndexChange(selectedRealIndex)
    }

    LazyColumn(
        state = listState,
        flingBehavior = snapFling,
        modifier = modifier.height(itemHeight * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = itemHeight)
    ) {
        items(INFINITE_MULTIPLIER * count) { flatIndex ->
            val realIndex = flatIndex % count
            val isSelected = realIndex == selectedRealIndex

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[realIndex],
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isSelected) 17.sp else 13.sp,
                    color = if (isSelected)
                        colorScheme.onBackground
                    else
                        colorScheme.onBackground.copy(alpha = 0.32f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ── Linear Wheel (year) ──────────────────────────────────────────────────────

@Composable
fun LinearWheelColumn(
    items: List<String>,
    initialIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 48.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFling = rememberSnapFlingBehavior(lazyListState = listState)

    val selectedIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    LaunchedEffect(selectedIndex) {
        onIndexChange(selectedIndex.coerceIn(0, items.lastIndex))
    }

    LazyColumn(
        state = listState,
        flingBehavior = snapFling,
        modifier = modifier.height(itemHeight * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = itemHeight)
    ) {
        items(items.size) { index ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[index],
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isSelected) 17.sp else 13.sp,
                    color = if (isSelected)
                        colorScheme.onBackground
                    else
                        colorScheme.onBackground.copy(alpha = 0.32f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
