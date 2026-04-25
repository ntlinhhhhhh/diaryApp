package com.diary.moonpage.presentation.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.util.*

@Composable
fun CalendarTopBar(
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left leaf icon + dropdown
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Eco,
                    contentDescription = "App Icon",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // Right icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.BreakfastDining,
                contentDescription = null,
                tint = Color(0xFFFFCC80),
                modifier = Modifier.size(28.dp).clickable { /* TODO */ }
            )
            Icon(
                imageVector = Icons.Rounded.Palette,
                contentDescription = null,
                tint = Color(0xFFFFE082),
                modifier = Modifier.size(28.dp).clickable { /* TODO */ }
            )
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp).clickable { /* TODO */ }
            )
        }
    }
}

@Composable
fun CalendarHeader() {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun DayItem(
    day: Int?,
    isSelected: Boolean,
    moodColor: Color?,
    moodIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    val baseMoodColor = moodColor ?: Color(0xFFF0F4F8) // Light grey/blue for empty days
    val finalMoodColor = if (isSelected && moodColor == null) {
        Color.Transparent
    } else {
        baseMoodColor
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable(enabled = day != null) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(CircleShape)
                .then(
                    if (isSelected && moodColor == null) 
                        Modifier.border(2.dp, Color(0xFF4CAF50), CircleShape) // Green border for selected empty day
                    else Modifier
                )
                .background(finalMoodColor),
            contentAlignment = Alignment.Center
        ) {
            if (moodIcon != null) {
                Icon(
                    imageVector = moodIcon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (day != null) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (isSelected && moodColor == null) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}

@Composable
fun DiaryEntryPreview(
    date: String,
    moodIcon: ImageVector,
    moodColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(moodColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = moodIcon,
                    contentDescription = null,
                    tint = moodColor,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
