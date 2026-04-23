package com.diary.moonpage.presentation.screens.moment

import android.net.Uri
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diary.moonpage.core.util.ImageUtils
import com.diary.moonpage.presentation.components.moment.MomentTag
import com.diary.moonpage.presentation.theme.MoonPageTheme
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentUploadScreen(
    capturedImageUri: Uri,
    capturedLensFacing: Int,
    pagerState: PagerState,
    allTags: List<MomentTag>,
    userMessage: String,
    onUserMessageChange: (String) -> Unit,
    userRating: Int,
    onUserRatingChange: (Int) -> Unit,
    userLocation: String,
    onLocationClick: () -> Unit,
    userWeather: String,
    onWeatherClick: () -> Unit,
    isLoading: Boolean,
    isSuccess: Boolean,
    onCancel: () -> Unit,
    onUpload: (File, String) -> Unit,
    onShowTagSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val onBgColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).height(56.dp)) {
            Text(
                text = "Upload",
                color = onBgColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = {
                    scope.launch {
                        try {
                            val inputStream = context.contentResolver.openInputStream(capturedImageUri)
                            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                            if (bitmap != null) {
                                ImageUtils.saveBitmapToGallery(context, bitmap)
                                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Rounded.FileDownload, null, tint = onBgColor, modifier = Modifier.size(36.dp))
            }
        }

        // Image Box
        Box(
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f).clip(RoundedCornerShape(32.dp)).background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = capturedImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = if (capturedLensFacing == CameraSelector.LENS_FACING_FRONT) -1f else 1f),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 4.dp), contentAlignment = Alignment.BottomCenter) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    contentPadding = PaddingValues(horizontal = 48.dp)
                ) { page ->
                    val tag = allTags[page]
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Surface(
                            color = tag.containerColor ?: Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (tag.id != "review") {
                                    tag.icon?.let { Icon(it, null, tint = tag.contentColor, modifier = Modifier.size(18.dp).clickable { onShowTagSheet() }) ; Spacer(modifier = Modifier.width(8.dp)) }
                                }
                                when(tag.id) {
                                    "text" -> {
                                        BasicTextField(
                                            value = userMessage,
                                            onValueChange = onUserMessageChange,
                                            textStyle = TextStyle(color = tag.contentColor, fontWeight = FontWeight.Medium, fontSize = 16.sp, textAlign = TextAlign.Center),
                                            cursorBrush = SolidColor(tag.contentColor),
                                            decorationBox = { innerTextField ->
                                                if (userMessage.isEmpty()) Text("Add a message", color = tag.contentColor.copy(alpha = 0.6f), fontSize = 16.sp)
                                                innerTextField()
                                            }
                                        )
                                    }
                                    "review" -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) { index ->
                                                val starRating = index + 1
                                                Icon(
                                                    imageVector = if (starRating <= userRating) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(24.dp).clickable { onUserRatingChange(starRating) }
                                                )
                                            }
                                        }
                                    }
                                    "location" -> {
                                        Text(text = if (userLocation.isNotEmpty()) userLocation else "Tap to add location", color = tag.contentColor, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onLocationClick() })
                                    }
                                    "weather" -> {
                                        Text(text = userWeather, color = tag.contentColor, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onWeatherClick() })
                                    }
                                    else -> Text(text = tag.text, color = tag.contentColor, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(allTags.size) { i ->
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(if (i == pagerState.currentPage) onBgColor else onBgColor.copy(alpha = 0.3f)))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, onBgColor.copy(alpha = 0.1f), CircleShape).clickable { onCancel() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Close, "Cancel", tint = onBgColor, modifier = Modifier.size(32.dp))
            }

            Box(
                modifier = Modifier.size(80.dp).border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape).padding(8.dp).clip(CircleShape).background(Color.Transparent).clickable(enabled = !isLoading && !isSuccess) {
                    val file = File(capturedImageUri.path!!)
                    val currentTag = allTags[pagerState.currentPage]
                    val caption = when(currentTag.id) {
                        "text" -> userMessage
                        "review" -> "Rating: $userRating stars"
                        "location" -> userLocation
                        "weather" -> userWeather
                        else -> currentTag.text
                    }
                    onUpload(file, caption)
                },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isSuccess,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "Success"
                ) { success ->
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(if (success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(if (success) Icons.Default.Check else Icons.Rounded.Send, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(36.dp))
                    }
                }
            }

            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).border(1.dp, onBgColor.copy(alpha = 0.1f), CircleShape).clickable { onShowTagSheet() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, "Tags", tint = onBgColor, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@ComposePreview(showBackground = true)
@Composable
fun MomentUploadScreenPreview() {
    MoonPageTheme {
        // MomentUploadScreen requires many states, this is a simplified preview
    }
}
