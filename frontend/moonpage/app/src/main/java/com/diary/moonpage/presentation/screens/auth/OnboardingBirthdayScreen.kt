package com.diary.moonpage.presentation.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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

private val MONTHS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)
private val DAYS = (1..31).map { it.toString() }
private val YEARS = (1950..2015).map { it.toString() }

@Composable
fun OnboardingBirthdayScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Default: 15th April 2000
    var selectedMonthIndex by remember { mutableIntStateOf(3) }   // April
    var selectedDayIndex   by remember { mutableIntStateOf(14) }  // 15
    var selectedYearIndex  by remember { mutableIntStateOf(50) }  // 2000

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
                TextButton(onClick = onSkip) {
                    Text(
                        "Skip",
                        color = colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Center selection highlight
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                colorScheme.primary.copy(alpha = 0.08f),
                                RoundedCornerShape(12.dp)
                            )
                    )
                    // Top + bottom fade dividers
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.Center).offset(y = (-26).dp),
                        color = colorScheme.primary.copy(alpha = 0.25f)
                    )
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.Center).offset(y = 26.dp),
                        color = colorScheme.primary.copy(alpha = 0.25f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WheelColumn(
                            items = MONTHS,
                            initialIndex = selectedMonthIndex,
                            onIndexChange = { selectedMonthIndex = it },
                            modifier = Modifier.weight(1f)
                        )
                        WheelColumn(
                            items = DAYS,
                            initialIndex = selectedDayIndex,
                            onIndexChange = { selectedDayIndex = it },
                            modifier = Modifier.weight(1f)
                        )
                        WheelColumn(
                            items = YEARS,
                            initialIndex = selectedYearIndex,
                            onIndexChange = { selectedYearIndex = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Next Button ───────────────────────────────────────────────────
            Button(
                onClick = {
                    val day   = DAYS[selectedDayIndex].padStart(2, '0')
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

// ── Reusable Wheel Picker Column ────────────────────────────────────────────

@Composable
fun WheelColumn(
    items: List<String>,
    initialIndex: Int,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 52.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val coroutineScope = rememberCoroutineScope()

    // 1 phantom item above & below so first/last items can be centered
    val paddedItems = listOf("") + items + listOf("")
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val selectedIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    // Report selection changes upstream
    LaunchedEffect(selectedIndex) {
        onIndexChange(selectedIndex.coerceIn(0, items.lastIndex))
    }

    // Snap to nearest integer position when scroll stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            coroutineScope.launch {
                listState.animateScrollToItem(listState.firstVisibleItemIndex)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.height(itemHeight * 3), // 3 visible items
        horizontalAlignment = Alignment.CenterHorizontally,
        userScrollEnabled = true
    ) {
        items(paddedItems.size) { index ->
            val realIndex = index - 1 // offset by 1 phantom
            val isSelected = realIndex == selectedIndex

            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (paddedItems[index].isNotEmpty()) {
                    Text(
                        text = paddedItems[index],
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = if (isSelected) 18.sp else 14.sp,
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
}
