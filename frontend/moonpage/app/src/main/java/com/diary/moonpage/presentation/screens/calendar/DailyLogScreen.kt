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
    LaunchedEffect(dateString) {
        viewModel.fetchLogForDate(dateString)
    }

    val existingLog by viewModel.existingLog.collectAsState()

    var selectedMood by remember { mutableStateOf<Int?>(null) }
    val selectedActivities = remember { mutableStateListOf<Int>() }
    var noteText by remember { mutableStateOf("") }
    var sleepHours by remember { mutableStateOf(7f) }

    // Pre-fill if editing existing log
    LaunchedEffect(existingLog) {
        existingLog?.let { log ->
            selectedMood = log.baseMoodId
            selectedActivities.clear()
            log.activityIds?.let { selectedActivities.addAll(it) }
            noteText = log.note ?: ""
        }
    }

    // Track if user has made any changes
    val hasChanges by remember {
        derivedStateOf {
            selectedMood != null || selectedActivities.isNotEmpty() || noteText.isNotBlank()
        }
    }

    // Unsaved changes dialog
    var showExitDialog by remember { mutableStateOf(false) }

    // Intercept back press
    BackHandler(enabled = hasChanges) {
        showExitDialog = true
    }

    // --- Unsaved Changes Confirmation Dialog ---
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFF2C2C2C),
            title = {
                Text(
                    text = "Unsaved Changes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "Changes have not been saved. Are you sure you want to exit?",
                    color = Color(0xFFAAAAAA),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Exit", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel", color = Color(0xFFAAAAAA))
                }
            }
        )
    }

    val moods = listOf(
        Pair(1, MoonIcons.Emotions.Happy),
        Pair(2, MoonIcons.Emotions.Calm),
        Pair(3, MoonIcons.Emotions.Anxious),
        Pair(4, MoonIcons.Emotions.Sad),
        Pair(5, MoonIcons.Emotions.Tired)
    )

    val hobbies = listOf(
        DailyActivity(101, "exercise", MoonIcons.Hobbies.Exercise),
        DailyActivity(102, "TV & content", MoonIcons.Hobbies.TvContent),
        DailyActivity(103, "movie", MoonIcons.Hobbies.Movie),
        DailyActivity(104, "gaming", MoonIcons.Hobbies.Gaming),
        DailyActivity(105, "reading", MoonIcons.Hobbies.Reading),
        DailyActivity(106, "walk", MoonIcons.Hobbies.Walk),
        DailyActivity(107, "music", MoonIcons.Hobbies.Music),
        DailyActivity(108, "drawing", MoonIcons.Hobbies.Drawing)
    )

    val emotions = listOf(
        DailyActivity(201, "excited", MoonIcons.Emotions.Excited),
        DailyActivity(202, "relaxed", MoonIcons.Emotions.Relaxed),
        DailyActivity(203, "proud", MoonIcons.Emotions.Proud),
        DailyActivity(204, "hopeful", MoonIcons.Emotions.Hopeful),
        DailyActivity(205, "happy", MoonIcons.Emotions.Happy),
        DailyActivity(206, "enthusiastic", MoonIcons.Emotions.Enthusiastic),
        DailyActivity(207, "pit-a-pat", MoonIcons.People.Partner),
        DailyActivity(208, "refreshed", MoonIcons.Weather.Windy),
        DailyActivity(209, "calm", MoonIcons.Weather.Cloudy),
        DailyActivity(210, "grateful", MoonIcons.Weather.Sunny),
        DailyActivity(211, "depressed", MoonIcons.Weather.Stormy),
        DailyActivity(212, "lonely", MoonIcons.Emotions.Lonely)
    )

    val meals = listOf(
        DailyActivity(301, "breakfast", MoonIcons.Meals.Breakfast),
        DailyActivity(302, "lunch", MoonIcons.Meals.Lunch),
        DailyActivity(303, "dinner", MoonIcons.Meals.Dinner),
        DailyActivity(304, "night snack", MoonIcons.Meals.NightSnack)
    )

    val selfCare = listOf(
        DailyActivity(401, "shower", MoonIcons.SelfCare.Shower),
        DailyActivity(402, "brush teeth", MoonIcons.SelfCare.BrushTeeth),
        DailyActivity(403, "wash face", MoonIcons.SelfCare.WashFace),
        DailyActivity(404, "drink water", MoonIcons.SelfCare.DrinkWater)
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (hasChanges) showExitDialog = true else onNavigateBack()
                }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Select date */ }
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(onClick = { /* Settings */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Done",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        containerColor = Color(0xFF202020)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                // How was your day
                Surface(
                    color = Color(0xFF2C2C2C),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "How was your day?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            moods.forEach { (id, moodIcon) ->
                                val isSelected = selectedMood == id
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF81C784) else Color(0xFF555555))
                                        .clickable { selectedMood = id },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = moodIcon.vector,
                                        contentDescription = null,
                                        tint = if (isSelected) Color(0xFF1E3A20) else Color(0xFF888888),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                DailyActivitySection(title = "Hobbies", items = hobbies, selectedIds = selectedActivities) { id ->
                    if (selectedActivities.contains(id)) selectedActivities.remove(id) else selectedActivities.add(id)
                }
            }

            item {
                DailyActivitySection(title = "Emotions", items = emotions, selectedIds = selectedActivities) { id ->
                    if (selectedActivities.contains(id)) selectedActivities.remove(id) else selectedActivities.add(id)
                }
            }

            item {
                DailyActivitySection(title = "Meals", items = meals, selectedIds = selectedActivities) { id ->
                    if (selectedActivities.contains(id)) selectedActivities.remove(id) else selectedActivities.add(id)
                }
            }

            item {
                DailyActivitySection(title = "Self-Care", items = selfCare, selectedIds = selectedActivities) { id ->
                    if (selectedActivities.contains(id)) selectedActivities.remove(id) else selectedActivities.add(id)
                }
            }

            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Music", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Link account", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF333333),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add a song", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Steps", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF2C2C2C),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 24.dp)
                        ) {
                            Icon(Icons.Rounded.DirectionsWalk, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("14 ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Text("steps", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

            // Sleep Section
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sleep", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${sleepHours.toInt()}h ${((sleepHours % 1) * 60).toInt()}m",
                            color = Color(0xFF81C784),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFF2C2C2C),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Bedtime,
                                    contentDescription = null,
                                    tint = Color(0xFF9575CD),
                                    modifier = Modifier.size(28.dp)
                                )
                                Slider(
                                    value = sleepHours,
                                    onValueChange = { sleepHours = it },
                                    valueRange = 0f..12f,
                                    steps = 23,
                                    modifier = Modifier.weight(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF9575CD),
                                        activeTrackColor = Color(0xFF9575CD),
                                        inactiveTrackColor = Color(0xFF555555)
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("0h", color = Color(0xFF888888), fontSize = 11.sp)
                                Text("6h", color = Color(0xFF888888), fontSize = 11.sp)
                                Text("12h", color = Color(0xFF888888), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Today's Note Section
            item {
                Column {
                    Text(
                        text = "Today's note",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Surface(
                        color = Color(0xFF2C2C2C),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            placeholder = {
                                Text(
                                    "Write about your day...",
                                    color = Color(0xFF666666),
                                    fontSize = 14.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color(0xFF4CAF50).copy(alpha = 0.6f),
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 8
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DailyActivitySection(
    title: String,
    items: List<DailyActivity>,
    selectedIds: List<Int>,
    onItemClick: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White
            )
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
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { item ->
            val isSelected = selectedIds.contains(item.id)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onItemClick(item.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFF1F8E9) else Color(0xFF333333)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon.vector,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFF4CAF50) else Color(0xFF888888),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.label,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}
