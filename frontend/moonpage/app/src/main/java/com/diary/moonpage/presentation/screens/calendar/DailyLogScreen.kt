package com.diary.moonpage.presentation.screens.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.core.util.MoonIcon
import com.diary.moonpage.core.util.MoonIcons
import com.diary.moonpage.core.util.ActivityPreferencesManager
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.rotate
import androidx.compose.runtime.rememberCoroutineScope

data class DailyActivity(val id: String, val label: String, val icon: MoonIcon)

@Composable
fun DailyLogScreen(
    dateString: String,
    onNavigateBack: () -> Unit,
    onDone: (String) -> Unit,
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentSelectedDate by remember { mutableStateOf(LocalDate.parse(dateString)) }
    
    LaunchedEffect(currentSelectedDate) { 
        viewModel.fetchLogForDate(currentSelectedDate.toString()) 
    }

    val existingLog by viewModel.existingLog.collectAsState()
    val enabledCategories by viewModel.enabledCategories.collectAsState()
    val dynamicActivities by viewModel.dynamicActivities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    var selectedMood by remember { mutableStateOf<Int?>(null) }
    val selectedActivities = remember { mutableStateListOf<String>() }
    var noteText by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf(7f) }

    LaunchedEffect(existingLog) {
        existingLog?.let { log ->
            selectedMood = log.baseMoodId
            selectedActivities.clear()
            log.activityIds?.let { selectedActivities.addAll(it) }
            noteText = log.note ?: ""
            sleepHours = log.sleepHours?.toFloat() ?: 7f
        }
    }

    val hasChanges by remember {
        derivedStateOf { selectedMood != null || selectedActivities.isNotEmpty() || noteText.isNotBlank() }
    }
    var showExitDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showOverwriteDialog by remember { mutableStateOf(false) }
    var pendingDate by remember { mutableStateOf<LocalDate?>(null) }

    BackHandler(enabled = hasChanges) { showExitDialog = true }

    // ── Unsaved Changes Dialog ──────────────────────────────────────────────
    if (showExitDialog) {
        Dialog(
            onDismissRequest = { showExitDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Changes have not been saved.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Are you sure you want to exit?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showExitDialog = false },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.surfaceVariant,
                                contentColor = colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Button(
                            onClick = { showExitDialog = false; onNavigateBack() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Exit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // ── Overwrite Warning Dialog ─────────────────────────────────────────────
    if (showOverwriteDialog) {
        Dialog(
            onDismissRequest = { showOverwriteDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Data already exists",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "There is already a record for this day. Do you want to overwrite it with your current changes?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showOverwriteDialog = false },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Cancel", color = colorScheme.onSurfaceVariant)
                        }
                        Button(
                            onClick = { 
                                showOverwriteDialog = false
                                pendingDate?.let { currentSelectedDate = it }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Overwrite", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ── Date Picker Dialog ──────────────────────────────────────────────────
    if (showDatePicker) {
        DailyLogDatePickerDialog(
            initialDate = currentSelectedDate,
            onDateSelected = { date ->
                showDatePicker = false
                if (date.isAfter(LocalDate.now())) {
                    scope.launch { snackbarHostState.showSnackbar("You cannot record logs for future dates!") }
                } else {
                    viewModel.checkLogExists(date.toString()) { exists ->
                        if (exists) {
                            pendingDate = date
                            showOverwriteDialog = true
                        } else {
                            currentSelectedDate = date
                        }
                    }
                }
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // ── Activity data definitions ─────────────────────────────────────────────

    val moods = listOf(
        Pair(1, MoonIcons.Moods.Happy),
        Pair(2, MoonIcons.Moods.Good),
        Pair(3, MoonIcons.Moods.Neutral),
        Pair(4, MoonIcons.Moods.Sad),
        Pair(5, MoonIcons.Moods.Angry)
    )

    val activitiesByCategory = remember(dynamicActivities) {
        dynamicActivities.groupBy { it.category }.mapValues { entry ->
            entry.value.map { activity ->
                DailyActivity(
                    id = activity.id,
                    label = activity.name,
                    icon = MoonIcons.getIconForActivity(activity.name)
                )
            }
        }
    }

    fun onToggle(id: String) {
        if (selectedActivities.contains(id)) selectedActivities.remove(id) else selectedActivities.add(id)
    }



    // ── Scaffold ──────────────────────────────────────────────────────────────

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (hasChanges) showExitDialog = true else onNavigateBack() }) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = colorScheme.onBackground)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentSelectedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")), 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold, 
                        color = colorScheme.onBackground
                    )
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onBackground)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = colorScheme.onBackground)
                }
            }
        },
        bottomBar = {
            Box {
                Button(
                    onClick = {
                        if (selectedMood == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please select a mood first!")
                            }
                            return@Button
                        }
                        
                        viewModel.saveDailyLog(
                            date = currentSelectedDate.toString(),
                            baseMoodId = selectedMood!!,
                            note = noteText.takeIf { it.isNotBlank() },
                            sleepHours = sleepHours.toDouble(),
                            isMenstruation = false,
                            activityIds = selectedActivities.toList(),
                            onSuccess = {
                                val msg = if (existingLog != null) "Record updated successfully!" else "Record created successfully!"
                                onDone(msg)
                            },
                            onFailure = { errorMsg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(errorMsg)
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = colorScheme.onPrimary,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("Done", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onPrimary)
                    }
                }
            }
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Mood ─────────────────────────────────────────────────────────
            item {
                Surface(color = colorScheme.surface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("How was your day?", color = colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            moods.forEach { (id, moodIcon) ->
                                val isSelected = selectedMood == id
                                val moodColor = MoonIcons.Moods.getMoodColor(id)
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape)
                                        .background(
                                            if (isSelected) moodColor 
                                            else moodColor.copy(alpha = 0.2f)
                                        )
                                        .clickable { selectedMood = id },
                                     contentAlignment = Alignment.Center
                                 ) {
                                     if (moodIcon.drawableRes != null) {
                                         Image(
                                             painter = painterResource(id = moodIcon.drawableRes),
                                             contentDescription = null,
                                             modifier = Modifier.size(if (isSelected) 32.dp else 28.dp)
                                         )
                                     } else if (moodIcon.vector != null) {
                                         Icon(
                                             moodIcon.vector, contentDescription = null,
                                             tint = if (isSelected) Color.White else moodColor,
                                             modifier = Modifier.size(24.dp)
                                         )
                                     }
                                 }
                            }
                        }
                    }
                }
            }

            // ── Activity sections (conditionally shown) ───────────────────────

            ActivityPreferencesManager.ALL_CATEGORY_KEYS.forEach { category ->
                if (category in enabledCategories) {
                    val categoryActivities = activitiesByCategory[category] ?: emptyList()
                    if (categoryActivities.isNotEmpty()) {
                        item {
                            val sectionTitle = if (category == "SelfCare") "Self-Care" else category
                            DailyActivitySection(
                                title = sectionTitle,
                                items = categoryActivities,
                                selectedIds = selectedActivities,
                                onItemClick = { onToggle(it) }
                            )
                        }
                    }
                }
            }

            // ── Music ─────────────────────────────────────────────────────────
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Music", color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Link account", color = colorScheme.onBackground.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp)) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add a song", color = colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Steps ─────────────────────────────────────────────────────────
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Steps", color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = colorScheme.surface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 24.dp)) {
                            Icon(Icons.Rounded.DirectionsWalk, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("14 ", color = colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("steps", color = colorScheme.onSurface, fontSize = 16.sp)
                        }
                    }
                }
            }

            // ── Sleep ─────────────────────────────────────────────────────────
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Sleep", color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("${sleepHours.toInt()}h ${((sleepHours % 1) * 60).toInt()}m", color = colorScheme.primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(color = colorScheme.surface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Rounded.Bedtime, contentDescription = null, tint = colorScheme.tertiary, modifier = Modifier.size(28.dp))
                                Slider(value = sleepHours, onValueChange = { sleepHours = it }, valueRange = 0f..12f, steps = 23, modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = colorScheme.primary,
                                        activeTrackColor = colorScheme.primary,
                                        inactiveTrackColor = colorScheme.onSurface.copy(alpha = 0.2f)
                                    ))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("0h", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 11.sp)
                                Text("6h", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 11.sp)
                                Text("12h", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ── Today's note ──────────────────────────────────────────────────
            item {
                Column {
                    Text("Today's note", color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    Surface(color = colorScheme.surface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = noteText, onValueChange = { noteText = it },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                            placeholder = { Text("Write about your day...", color = colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 14.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = colorScheme.primary.copy(alpha = 0.6f),
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp), maxLines = 8
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── Shared composables ────────────────────────────────────────────────────────

@Composable
fun DailyActivitySection(
    title: String,
    items: List<DailyActivity>,
    selectedIds: List<String>,
    onItemClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var isCollapsed by remember { mutableStateOf(false) }
    val rotation by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isCollapsed) -90f else 0f,
        label = "arrowRotation"
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isCollapsed = !isCollapsed }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.rotate(rotation)
            )
        }
        
        AnimatedVisibility(
            visible = !isCollapsed,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                DailyLogGrid(items = items, selectedIds = selectedIds, onItemClick = onItemClick)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyLogGrid(
    items: List<DailyActivity>,
    selectedIds: List<String>,
    onItemClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { item ->
            val isSelected = selectedIds.contains(item.id)
            val iconColor = item.icon.color
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(72.dp).clickable { onItemClick(item.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) iconColor.copy(alpha = 0.18f)
                            else colorScheme.onSurface.copy(alpha = 0.08f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.icon.drawableRes != null) {
                        Image(
                            painter = painterResource(id = item.icon.drawableRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .alpha(if (isSelected) 1f else 0.4f)
                        )
                    } else if (item.icon.vector != null) {
                        Icon(
                            item.icon.vector, contentDescription = null,
                            tint = if (isSelected) iconColor else colorScheme.onSurface.copy(alpha = 0.35f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    item.label,
                    color = if (isSelected) iconColor else colorScheme.onBackground.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    maxLines = 1,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun DailyLogDatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val initialPage = 500 * 12
    val baseYearMonth = YearMonth.from(initialDate)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { initialPage * 2 }
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Select Date",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val offset = page - initialPage
                    val pageYearMonth = baseYearMonth.plusMonths(offset.toLong())
                    val daysInMonth = (1..pageYearMonth.lengthOfMonth()).toList()
                    val firstDayOfMonth = pageYearMonth.atDay(1)
                    val firstDayOffset = if (firstDayOfMonth.dayOfWeek == java.time.DayOfWeek.SUNDAY) 0 else firstDayOfMonth.dayOfWeek.value
                    
                    Column {
                        // Month Header in Picker
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                pageYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(7),
                            modifier = Modifier.height(260.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(firstDayOffset) { Spacer(Modifier) }
                            items(daysInMonth) { day ->
                                val date = pageYearMonth.atDay(day)
                                val isFuture = date.isAfter(LocalDate.now())
                                val isSelected = date == initialDate
                                val isToday = date == LocalDate.now()
                                
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> colorScheme.primary
                                                isToday -> colorScheme.primary.copy(alpha = 0.1f)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable(enabled = !isFuture) { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = when {
                                            isSelected -> colorScheme.onPrimary
                                            isFuture -> colorScheme.onSurface.copy(alpha = 0.2f)
                                            isToday -> colorScheme.primary
                                            else -> colorScheme.onSurface
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
