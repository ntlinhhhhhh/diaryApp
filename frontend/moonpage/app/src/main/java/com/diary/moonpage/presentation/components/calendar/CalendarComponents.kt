package com.diary.moonpage.presentation.components.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import java.time.LocalDate
import java.util.*

@Composable
fun CalendarTopBar(
    onFilterClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onThemeClick: () -> Unit = {},
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
                contentDescription = "Theme",
                tint = Color(0xFFFFE082),
                modifier = Modifier.size(28.dp).clickable { onThemeClick() }
            )
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp).clickable { onSettingsClick() }
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
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
    moodDrawable: Int? = null,
    isToday: Boolean = false,
    isDimmed: Boolean = false,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    // Empty offset cell – just blank space, no circle
    if (day == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 4.dp)
        ) {
            // Invisible placeholder có cùng chiều cao để grid thẳng hàng
            Box(modifier = Modifier.size(42.dp).align(Alignment.Center))
            Spacer(modifier = Modifier.height(2.dp + 14.dp)) // circle + number area
        }
        return
    }

    val emptyDayBg = colorScheme.surfaceVariant.copy(alpha = 0.7f)

    val circleBg = when {
        moodColor != null -> moodColor
        isSelected        -> Color.Transparent
        else              -> emptyDayBg
    }

    val animatedBg by animateColorAsState(
        targetValue = circleBg,
        animationSpec = tween(200),
        label = "dayBg"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        // ── Circle cố định 42dp ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onClick() }
                .then(
                    when {
                        isSelected && moodColor == null ->
                            Modifier.border(2.dp, colorScheme.primary, CircleShape)
                        isToday && moodColor == null ->
                            Modifier.border(2.dp, colorScheme.primary, CircleShape)
                        else -> Modifier
                    }
                )
                .background(animatedBg),
            contentAlignment = Alignment.Center
        ) {
            if (moodDrawable != null) {
                Image(
                    painter = painterResource(id = moodDrawable),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .then(if (isDimmed) Modifier.alpha(0.55f) else Modifier)
                )
            } else if (moodIcon != null) {
                Icon(
                    imageVector = moodIcon,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = if (isDimmed) 0.3f else 0.55f),
                    modifier = Modifier.fillMaxSize(0.52f)
                )
            }

            // Small catch-up indicator (e.g. clock or history icon)

        }

        // ── Số ngày bên dưới ─────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = when {
                isToday    -> FontWeight.ExtraBold
                isSelected -> FontWeight.Bold
                else       -> FontWeight.Normal
            },
            color = when {
                isSelected && moodColor == null -> colorScheme.primary
                isToday                         -> colorScheme.primary
                else                            -> colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
    }
}




@Composable
fun DiaryEntryPreview(
    date: String,
    moodIcon: ImageVector? = null,
    moodDrawable: Int? = null,
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
                if (moodDrawable != null) {
                    Image(
                        painter = painterResource(id = moodDrawable),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                } else if (moodIcon != null) {
                    Icon(
                        imageVector = moodIcon,
                        contentDescription = null,
                        tint = moodColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
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

/**
 * Bottom sheet showing a diary "post card" for the selected day.
 * Shows: mood icon, date, full note, recorded activities, and Edit/Delete/Share actions.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DayDetailBottomSheet(
    date: LocalDate,
    moodIcon: ImageVector? = null,
    moodDrawable: Int? = null,
    moodColor: Color,
    moodLabel: String,
    noteSnippet: String?,
    activityNames: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = cs.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(cs.onSurface.copy(alpha = 0.15f), CircleShape)
            )
        }
    ) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Header: mood + date ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(moodColor.copy(alpha = 0.10f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(moodColor.copy(alpha = 0.22f), CircleShape),
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
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = moodLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = cs.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}, ${date.dayOfMonth} ${date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${date.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurface.copy(alpha = 0.55f)
                        )
                    }
                }
            }

            // ── Activities ──────────────────────────────────────────────────
            if (activityNames.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            "Activities",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            activityNames.forEach { name ->
                                val icon = com.diary.moonpage.core.util.MoonIcons.getIconForActivity(name)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = cs.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(1.dp, icon.color.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (icon.drawableRes != null) {
                                            Image(
                                                painter = painterResource(id = icon.drawableRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (icon.vector != null) {
                                            Icon(
                                                imageVector = icon.vector,
                                                contentDescription = null,
                                                tint = icon.color,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = cs.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Note ─────────────────────────────────────────────────────────
            if (!noteSnippet.isNullOrBlank()) {
                item {
                    Column {
                        Text(
                            "Note",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = cs.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = cs.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = noteSnippet,
                                style = MaterialTheme.typography.bodyMedium,
                                color = cs.onSurface.copy(alpha = 0.85f),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }

            // ── Action buttons ───────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Share
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.onSurface)
                    ) {
                        Icon(Icons.Rounded.IosShare, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 13.sp)
                    }
                    // Edit
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = cs.onPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = cs.onPrimary, fontSize = 13.sp)
                    }
                    // Delete
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.error),
                        border = androidx.compose.foundation.BorderStroke(1.dp, cs.error.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
