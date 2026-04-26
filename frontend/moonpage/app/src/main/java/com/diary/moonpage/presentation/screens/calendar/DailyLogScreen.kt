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

data class DailyActivity(val id: Int, val label: String, val icon: MoonIcon)

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
    val colorScheme = MaterialTheme.colorScheme

    var selectedMood by remember { mutableStateOf<Int?>(null) }
    val selectedActivities = remember { mutableStateListOf<Int>() }
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
        Pair(1, MoonIcons.Emotions.Happy),
        Pair(2, MoonIcons.Emotions.Calm),
        Pair(3, MoonIcons.Emotions.Anxious),
        Pair(4, MoonIcons.Emotions.Sad),
        Pair(5, MoonIcons.Emotions.Tired)
    )

    val hobbies = listOf(
        DailyActivity(101, "exercise",    MoonIcons.Hobbies.Exercise),
        DailyActivity(102, "TV & content",MoonIcons.Hobbies.TvContent),
        DailyActivity(103, "movie",       MoonIcons.Hobbies.Movie),
        DailyActivity(104, "gaming",      MoonIcons.Hobbies.Gaming),
        DailyActivity(105, "reading",     MoonIcons.Hobbies.Reading),
        DailyActivity(106, "walk",        MoonIcons.Hobbies.Walk),
        DailyActivity(107, "music",       MoonIcons.Hobbies.Music),
        DailyActivity(108, "drawing",     MoonIcons.Hobbies.Drawing)
    )
    val emotions = listOf(
        DailyActivity(201, "excited",     MoonIcons.Emotions.Excited),
        DailyActivity(202, "relaxed",     MoonIcons.Emotions.Relaxed),
        DailyActivity(203, "proud",       MoonIcons.Emotions.Proud),
        DailyActivity(204, "hopeful",     MoonIcons.Emotions.Hopeful),
        DailyActivity(205, "happy",       MoonIcons.Emotions.Happy),
        DailyActivity(206, "enthusiastic",MoonIcons.Emotions.Enthusiastic),
        DailyActivity(207, "pit-a-pat",   MoonIcons.People.Partner),
        DailyActivity(208, "refreshed",   MoonIcons.Weather.Windy),
        DailyActivity(209, "calm",        MoonIcons.Weather.Cloudy),
        DailyActivity(210, "grateful",    MoonIcons.Weather.Sunny),
        DailyActivity(211, "depressed",   MoonIcons.Weather.Stormy),
        DailyActivity(212, "lonely",      MoonIcons.Emotions.Lonely)
    )
    val meals = listOf(
        DailyActivity(301, "breakfast",   MoonIcons.Meals.Breakfast),
        DailyActivity(302, "lunch",       MoonIcons.Meals.Lunch),
        DailyActivity(303, "dinner",      MoonIcons.Meals.Dinner),
        DailyActivity(304, "night snack", MoonIcons.Meals.NightSnack)
    )
    val selfCare = listOf(
        DailyActivity(401, "shower",      MoonIcons.SelfCare.Shower),
        DailyActivity(402, "brush teeth", MoonIcons.SelfCare.BrushTeeth),
        DailyActivity(403, "wash face",   MoonIcons.SelfCare.WashFace),
        DailyActivity(404, "drink water", MoonIcons.SelfCare.DrinkWater)
    )
    val chores = listOf(
        DailyActivity(501, "cleaning",    MoonIcons.Chores.Cleaning),
        DailyActivity(502, "cooking",     MoonIcons.Chores.Cooking),
        DailyActivity(503, "laundry",     MoonIcons.Chores.Laundry),
        DailyActivity(504, "dishes",      MoonIcons.Chores.Dishes)
    )
    val events = listOf(
        DailyActivity(601, "stay home",  MoonIcons.Events.StayHome),
        DailyActivity(602, "school",     MoonIcons.Events.School),
        DailyActivity(603, "restaurant", MoonIcons.Events.Restaurant),
        DailyActivity(604, "cafe",       MoonIcons.Events.Cafe),
        DailyActivity(605, "shopping",   MoonIcons.Events.Shopping),
        DailyActivity(606, "travel",     MoonIcons.Events.Travel),
        DailyActivity(607, "party",      MoonIcons.Events.Party),
        DailyActivity(608, "cinema",     MoonIcons.Events.Cinema)
    )
    val people = listOf(
        DailyActivity(701, "friends",    MoonIcons.People.Friends),
        DailyActivity(702, "family",     MoonIcons.People.Family),
        DailyActivity(703, "partner",    MoonIcons.People.Partner),
        DailyActivity(704, "alone",      MoonIcons.People.None)
    )
    val beauty = listOf(
        DailyActivity(801, "hair",       MoonIcons.Beauty.Hair),
        DailyActivity(802, "nails",      MoonIcons.Beauty.Nails),
        DailyActivity(803, "skincare",   MoonIcons.Beauty.Skincare),
        DailyActivity(804, "makeup",     MoonIcons.Beauty.Makeup)
    )
    val weatherActivities = listOf(
        DailyActivity(901, "sunny",      MoonIcons.Weather.Sunny),
        DailyActivity(902, "cloudy",     MoonIcons.Weather.Cloudy),
        DailyActivity(903, "rainy",      MoonIcons.Weather.Rainy),
        DailyActivity(904, "snowy",      MoonIcons.Weather.Snowy),
        DailyActivity(905, "windy",      MoonIcons.Weather.Windy),
        DailyActivity(906, "stormy",     MoonIcons.Weather.Stormy),
        DailyActivity(907, "hot",        MoonIcons.Weather.Hot),
        DailyActivity(908, "cold",       MoonIcons.Weather.Cold)
    )
    val health = listOf(
        DailyActivity(1001, "sick",      MoonIcons.Health.Sick),
        DailyActivity(1002, "hospital",  MoonIcons.Health.Hospital),
        DailyActivity(1003, "checkup",   MoonIcons.Health.Checkup),
        DailyActivity(1004, "medicine",  MoonIcons.Health.Medicine)
    )
    val work = listOf(
        DailyActivity(1101, "work",       MoonIcons.Work.Work),
        DailyActivity(1102, "end on time",MoonIcons.Work.EndOnTime),
        DailyActivity(1103, "overtime",   MoonIcons.Work.Overtime),
        DailyActivity(1104, "vacation",   MoonIcons.Work.Vacation)
    )
    val other = listOf(
        DailyActivity(1201, "snack",    MoonIcons.Other.Snack),
        DailyActivity(1202, "coffee",   MoonIcons.Other.Coffee),
        DailyActivity(1203, "beverage", MoonIcons.Other.Beverage),
        DailyActivity(1204, "tea",      MoonIcons.Other.Tea),
        DailyActivity(1205, "alcohol",  MoonIcons.Other.Alcohol),
        DailyActivity(1206, "smoking",  MoonIcons.Other.Smoking)
    )
    val school = listOf(
        DailyActivity(1301, "class",    MoonIcons.School.Class),
        DailyActivity(1302, "study",    MoonIcons.School.Study),
        DailyActivity(1303, "homework", MoonIcons.School.Homework),
        DailyActivity(1304, "exam",     MoonIcons.School.Exam)
    )
    val relationship = listOf(
        DailyActivity(1401, "date",        MoonIcons.Relationship.Date),
        DailyActivity(1402, "anniversary", MoonIcons.Relationship.Anniversary),
        DailyActivity(1403, "gift",        MoonIcons.Relationship.Gift),
        DailyActivity(1404, "conflict",    MoonIcons.Relationship.Conflict),
        DailyActivity(1405, "sex",         MoonIcons.Relationship.Sex)
    )

    fun onToggle(id: Int) {
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
                                    Icon(moodIcon.vector, contentDescription = null,
                                        tint = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Activity sections (conditionally shown) ───────────────────────

            if ("Hobbies" in enabledCategories) {
                item { DailyActivitySection("Hobbies", hobbies, selectedActivities) { onToggle(it) } }
            }
            if ("Emotions" in enabledCategories) {
                item { DailyActivitySection("Emotions", emotions, selectedActivities) { onToggle(it) } }
            }
            if ("Meals" in enabledCategories) {
                item { DailyActivitySection("Meals", meals, selectedActivities) { onToggle(it) } }
            }
            if ("SelfCare" in enabledCategories) {
                item { DailyActivitySection("Self-Care", selfCare, selectedActivities) { onToggle(it) } }
            }
            if ("Chores" in enabledCategories) {
                item { DailyActivitySection("Chores", chores, selectedActivities) { onToggle(it) } }
            }
            if ("Events" in enabledCategories) {
                item { DailyActivitySection("Events", events, selectedActivities) { onToggle(it) } }
            }
            if ("People" in enabledCategories) {
                item { DailyActivitySection("People", people, selectedActivities) { onToggle(it) } }
            }
            if ("Beauty" in enabledCategories) {
                item { DailyActivitySection("Beauty", beauty, selectedActivities) { onToggle(it) } }
            }
            if ("Weather" in enabledCategories) {
                item { DailyActivitySection("Weather", weatherActivities, selectedActivities) { onToggle(it) } }
            }
            if ("Health" in enabledCategories) {
                item { DailyActivitySection("Health", health, selectedActivities) { onToggle(it) } }
            }
            if ("Work" in enabledCategories) {
                item { DailyActivitySection("Work", work, selectedActivities) { onToggle(it) } }
            }
            if ("Other" in enabledCategories) {
                item { DailyActivitySection("Other", other, selectedActivities) { onToggle(it) } }
            }
            if ("School" in enabledCategories) {
                item { DailyActivitySection("School", school, selectedActivities) { onToggle(it) } }
            }
            if ("Relationship" in enabledCategories) {
                item { DailyActivitySection("Relationship", relationship, selectedActivities) { onToggle(it) } }
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
    selectedIds: List<Int>,
    onItemClick: (Int) -> Unit
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
    selectedIds: List<Int>,
    onItemClick: (Int) -> Unit
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
                    Icon(item.icon.vector, contentDescription = null,
                        tint = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.label, color = colorScheme.onBackground, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}
