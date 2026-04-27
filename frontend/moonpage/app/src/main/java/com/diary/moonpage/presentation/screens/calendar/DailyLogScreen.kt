package com.diary.moonpage.presentation.screens.calendar

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.core.util.MoonIcon
import com.diary.moonpage.core.util.MoonIcons
import com.diary.moonpage.core.util.ActivityPreferencesManager
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

data class DailyActivity(val id: String, val label: String, val icon: MoonIcon)

@Composable
fun DailyLogScreen(
    dateString: String,
    onNavigateBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    LaunchedEffect(dateString) { viewModel.fetchLogForDate(dateString) }

    val existingLog by viewModel.existingLog.collectAsState()
    val enabledCategories by viewModel.enabledCategories.collectAsState()
    val dynamicActivities by viewModel.dynamicActivities.collectAsState()
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
        }
    }

    val hasChanges by remember {
        derivedStateOf { selectedMood != null || selectedActivities.isNotEmpty() || noteText.isNotBlank() }
    }
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = hasChanges) { showExitDialog = true }

    // Unsaved Changes Dialog – theme-aware
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = colorScheme.surface,
            title = {
                Text("Unsaved Changes", color = colorScheme.onSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            },
            text = {
                Text("Changes have not been saved. Are you sure you want to exit?", color = colorScheme.onSurface.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onNavigateBack() }) {
                    Text("Exit", color = colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel", color = colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
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
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (hasChanges) showExitDialog = true else onNavigateBack() }) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = colorScheme.onBackground)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {}) {
                    Text(dateString, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onBackground)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = colorScheme.onBackground)
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    val moodId = selectedMood ?: 3
                    viewModel.saveDailyLog(
                        date = dateString,
                        baseMoodId = moodId,
                        note = noteText.takeIf { it.isNotBlank() },
                        sleepHours = sleepHours.toDouble(),
                        isMenstruation = false,
                        activityIds = selectedActivities.toList(),
                        onSuccess = onDone
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Done", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onPrimary)
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
                                Box(
                                    modifier = Modifier.size(56.dp).clip(CircleShape)
                                        .background(if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant)
                                        .clickable { selectedMood = id },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (moodIcon.drawableRes != null) {
                                        Image(
                                            painter = painterResource(id = moodIcon.drawableRes),
                                            contentDescription = null,
                                            modifier = Modifier.size(if (isSelected) 40.dp else 36.dp)
                                        )
                                    } else if (moodIcon.vector != null) {
                                        Icon(
                                            moodIcon.vector, contentDescription = null,
                                            tint = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(32.dp)
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
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = colorScheme.onBackground, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = null, tint = colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.height(16.dp))
        DailyLogGrid(items = items, selectedIds = selectedIds, onItemClick = onItemClick)
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(72.dp).clickable { onItemClick(item.id) }
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape)
                        .background(if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.icon.drawableRes != null) {
                        Image(
                            painter = painterResource(id = item.icon.drawableRes),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    } else if (item.icon.vector != null) {
                        Icon(
                            item.icon.vector, contentDescription = null,
                            tint = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.label, color = colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
