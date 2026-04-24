package com.diary.moonpage.presentation.components.moment

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.diary.moonpage.data.remote.api.MomentResponse
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MomentFeedItem(moment: MomentResponse, localPath: String? = null) {
    val onBgColor = MaterialTheme.colorScheme.onBackground
    val context = LocalContext.current
    
    val imageData = if (localPath != null && File(localPath).exists()) {
        File(localPath)
    } else {
        moment.imageUrl
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Space for top header
        Spacer(modifier = Modifier.height(72.dp))

        // Image Frame
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(onBgColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageData)
                    .crossfade(true)
                    .crossfade(200)
                    .diskCacheKey(moment.imageUrl)
                    .memoryCacheKey(moment.imageUrl)
                    .precision(Precision.INEXACT)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            if (!moment.caption.isNullOrEmpty()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 24.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = moment.caption,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Meta Tags (Rating, Location, Weather)
        FlowRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3
        ) {
            // Rating Tag
            if (moment.rating != null && moment.rating > 0) {
                TagChip(
                    tag = MomentTag(
                        id = "rating",
                        icon = Icons.Rounded.Star,
                        text = "${moment.rating}",
                        containerColor = Color(0xFFFFD700).copy(alpha = 0.2f),
                        contentColor = Color(0xFFFFA000)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Location Tag
            if (!moment.location.isNullOrEmpty()) {
                TagChip(
                    tag = MomentTag(
                        id = "location",
                        icon = Icons.Rounded.LocationOn,
                        text = moment.location,
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.2f),
                        contentColor = Color(0xFF1976D2)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Weather Tag
            if (!moment.weather.isNullOrEmpty()) {
                TagChip(
                    tag = MomentTag(
                        id = "weather",
                        icon = Icons.Rounded.WbSunny,
                        text = moment.weather,
                        containerColor = Color(0xFF00BCD4).copy(alpha = 0.2f),
                        contentColor = Color(0xFF0097A7)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Time Tag
            TagChip(
                tag = MomentTag(
                    id = "time",
                    icon = Icons.Rounded.AccessTime,
                    text = formatShortTime(moment.capturedAt),
                    containerColor = onBgColor.copy(alpha = 0.1f),
                    contentColor = onBgColor.copy(alpha = 0.6f)
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.25f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Me",
                color = onBgColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatRelativeTime(moment.capturedAt),
                color = onBgColor.copy(alpha = 0.5f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatShortTime(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = sdf.parse(dateString) ?: return ""
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        ""
    }
}

private fun formatRelativeTime(dateString: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = sdf.parse(dateString) ?: return ""
        val now = Date()
        val diffMillis = now.time - date.time
        
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        when {
            minutes < 1 -> "now"
            minutes < 60 -> "${minutes}m"
            hours < 24 -> "${hours}h"
            else -> "${days}d"
        }
    } catch (e: Exception) {
        ""
    }
}
