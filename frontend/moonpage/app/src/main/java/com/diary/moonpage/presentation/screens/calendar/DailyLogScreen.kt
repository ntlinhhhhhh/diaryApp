package com.diary.moonpage.presentation.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.core.util.MoonIcon
import com.diary.moonpage.core.util.MoonIcons

@Composable
fun DailyLogScreen(
    dateString: String,
    onNavigateBack: () -> Unit,
    onDone: () -> Unit
) {
    var selectedMood by remember { mutableStateOf<Int?>(2) } // Default to neutral

    // Allow multiple selection
    val selectedHobbies = remember { mutableStateListOf<MoonIcon>() }
    val selectedEmotions = remember { mutableStateListOf<MoonIcon>() }

    val moods = listOf(
        MoonIcons.Emotions.Happy,
        MoonIcons.Emotions.Calm, // Using as proxy for slight smile
        MoonIcons.Emotions.Anxious, // Proxy for neutral
        MoonIcons.Emotions.Sad,
        MoonIcons.Emotions.Tired // Proxy for very sad
    )

    val hobbies = listOf(
        MoonIcons.Hobbies.Exercise,
        MoonIcons.Hobbies.Movies, // Proxy for TV
        MoonIcons.Hobbies.Movies,
        MoonIcons.Hobbies.Gaming,
        MoonIcons.Hobbies.Reading,
        MoonIcons.Hobbies.Traveling, // Proxy for walk
        MoonIcons.Hobbies.Music,
        MoonIcons.Hobbies.Drawing
    )

    val emotions = listOf(
        MoonIcons.Emotions.Excited,
        MoonIcons.Health.Meditation, // Relaxed
        MoonIcons.Emotions.Happy, // Proud
        MoonIcons.Emotions.Calm, // Hopeful
        MoonIcons.Emotions.Happy,
        MoonIcons.Emotions.Excited,
        MoonIcons.Social.Partner, // Pit-a-pat
        MoonIcons.Weather.Windy, // Refreshed
        MoonIcons.Weather.Cloudy, // Calm
        MoonIcons.Weather.Sunny, // Grateful
        MoonIcons.Weather.Stormy, // Depressed
        MoonIcons.Social.Alone // Lonely
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
                IconButton(onClick = onNavigateBack) {
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
                        text = "Saturday, April 25", // Hardcoded to match UI for now, you can parse dateString
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
                onClick = onDone,
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
        containerColor = Color(0xFF202020) // Dark background matching the screenshot
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
                            moods.forEachIndexed { index, mood ->
                                val isSelected = selectedMood == index
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF81C784) else Color(0xFF555555))
                                        .clickable { selectedMood = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = mood.vector,
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
                // Hobbies
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hobbies",
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

                    DailyLogGrid(items = hobbies, selectedItems = selectedHobbies) {
                        if (selectedHobbies.contains(it)) {
                            selectedHobbies.remove(it)
                        } else {
                            selectedHobbies.add(it)
                        }
                    }
                }
            }

            item {
                // Emotions
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Emotions",
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

                    DailyLogGrid(items = emotions, selectedItems = selectedEmotions) {
                        if (selectedEmotions.contains(it)) {
                            selectedEmotions.remove(it)
                        } else {
                            selectedEmotions.add(it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyLogGrid(
    items: List<MoonIcon>,
    selectedItems: List<MoonIcon>,
    onItemClick: (MoonIcon) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items.forEach { item ->
            val isSelected = selectedItems.contains(item)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onItemClick(item) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFF1F8E9) else Color(0xFF333333)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.vector,
                        contentDescription = null,
                        tint = if (isSelected) Color(0xFF4CAF50) else Color(0xFF888888),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Hardcoded labels for layout simulation
                Text(
                    text = "label",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}
