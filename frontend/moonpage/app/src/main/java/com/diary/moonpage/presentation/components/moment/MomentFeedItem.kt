package com.diary.moonpage.presentation.components.moment

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp).height(56.dp))
        Spacer(modifier = Modifier.height(60.dp))

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
                    .precision(Precision.INEXACT)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            val inferredTag = remember(moment) {
                when {
                    moment.rating != null && moment.rating > 0 -> MomentTag("review", Icons.Rounded.Star, "Review", Color.White, Color.Yellow.copy(0.6f))
                    moment.caption?.startsWith("Rating: ") == true -> MomentTag("review", Icons.Rounded.Star, "Review", Color.White, Color.Yellow.copy(0.6f))
                    moment.location != null -> MomentTag("location", Icons.Rounded.LocationOn, "Location", Color.White, Color.Blue.copy(0.6f))
                    moment.weather != null -> MomentTag("weather", Icons.Rounded.Cloud, "Weather", Color.White, Color.Cyan.copy(0.6f))
                    moment.caption in listOf("Sunny ☀️", "Cloudy ☁️", "Rainy 🌧️", "Snowy ❄️", "Windy 💨") -> MomentTag("weather", Icons.Rounded.Cloud, "Weather", Color.White, Color.Cyan.copy(0.6f))
                    moment.caption == "Party Time!" -> MomentTag("party", null, "Party Time!", containerColor = Color(0xFF80FFE8), contentColor = Color.Black)
                    moment.caption == "OOTD" -> MomentTag("ootd", null, "OOTD", containerColor = Color.White, contentColor = Color.Black)
                    moment.caption == "Miss you" -> MomentTag("missyou", null, "Miss you", containerColor = Color(0xFFFF4B4B), contentColor = Color.White)
                    else -> MomentTag("text", null, "Message", Color.White, Color.Black.copy(0.6f))
                }
            }

            // Tags overlay like in UploadScreen
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 4.dp), contentAlignment = Alignment.BottomCenter) {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        val textToShow = when (inferredTag.id) {
                            "review" -> ""
                            "location" -> moment.location ?: moment.caption ?: ""
                            "weather" -> moment.weather ?: moment.caption ?: ""
                            else -> moment.caption ?: ""
                        }
                        
                        val shouldShow = inferredTag.id == "review" || textToShow.isNotEmpty() || inferredTag.icon != null

                        if (shouldShow) {
                            Surface(
                                color = inferredTag.containerColor ?: Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (inferredTag.id != "review") {
                                        inferredTag.icon?.let { 
                                            Icon(it, null, tint = inferredTag.contentColor, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                    }

                                    if (inferredTag.id == "review") {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val parsedRating = if (moment.caption?.startsWith("Rating: ") == true) {
                                                moment.caption.removePrefix("Rating: ").removeSuffix(" stars").toFloatOrNull() ?: 0f
                                            } else 0f
                                            val finalRating = if (moment.rating != null && moment.rating > 0) moment.rating else parsedRating

                                            repeat(5) { index ->
                                                val starIndex = index + 1
                                                val starIcon = when {
                                                    finalRating >= starIndex -> Icons.Default.Star
                                                    finalRating >= starIndex - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                                                    else -> Icons.Default.StarBorder
                                                }
                                                Icon(
                                                    imageVector = starIcon,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        if (textToShow.isNotEmpty()) {
                                            Text(
                                                text = textToShow,
                                                color = inferredTag.contentColor,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TagChip(
            tag = MomentTag(
                id = "time",
                icon = Icons.Rounded.AccessTime,
                text = formatShortTime(moment.capturedAt),
                containerColor = onBgColor.copy(alpha = 0.1f),
                contentColor = onBgColor.copy(alpha = 0.6f)
            )
        )

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
                fontWeight = FontWeight.Bold,
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
