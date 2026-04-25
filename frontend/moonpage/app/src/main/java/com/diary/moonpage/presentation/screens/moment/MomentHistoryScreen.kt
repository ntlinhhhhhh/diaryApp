package com.diary.moonpage.presentation.screens.moment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.imageLoader
import coil.request.ImageRequest
import com.diary.moonpage.core.theme.MoonPageTheme
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.presentation.components.moment.MomentFeedItem
import com.diary.moonpage.presentation.components.moment.CaptureButton
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Close

@Composable
fun MomentHistoryScreen(
    moments: List<MomentResponse>,
    localPaths: Map<String, String>,
    onNavigateToGallery: () -> Unit,
    onBackToCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedMoments = remember(moments) { moments.sortedByDescending { it.capturedAt } }
    val feedPagerState = rememberPagerState(pageCount = { sortedMoments.size })
    val onBgColor = MaterialTheme.colorScheme.onBackground
    val bgColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    // Pre-fetch images
    LaunchedEffect(sortedMoments) {
        sortedMoments.take(5).forEach { moment ->
            if (localPaths[moment.imageUrl] == null) {
                val request = ImageRequest.Builder(context)
                    .data(moment.imageUrl)
                    .diskCacheKey(moment.imageUrl)
                    .memoryCacheKey(moment.imageUrl)
                    .build()
                context.imageLoader.enqueue(request)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        if (sortedMoments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No moments yet", color = onBgColor.copy(alpha = 0.6f))
            }
        } else {
            VerticalPager(
                state = feedPagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { index ->
                val moment = sortedMoments[index]
                MomentFeedItem(
                    moment = moment, 
                    localPath = localPaths[moment.imageUrl]
                )
            }
        }

        // Header (Chỉ còn icon bên trái)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(56.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.15f))
                    .align(Alignment.CenterStart)
            )
        }

        // Bottom Bar (Nút Menu được chuyển xuống đây)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 40.dp, vertical = 40.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Gallery
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.1f))
                    .clickable { onNavigateToGallery() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.GridView, null, tint = onBgColor, modifier = Modifier.size(28.dp))
            }

            // Nút Capture (Back to Camera)
            CaptureButton(onClick = onBackToCamera)

            // Nút Menu (Đổi thành MoreHoriz)
            if (sortedMoments.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(onBgColor.copy(alpha = 0.1f))
                        .clickable { showMenu = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.MoreHoriz, null, tint = onBgColor, modifier = Modifier.size(28.dp))
                }
            } else {
                Spacer(modifier = Modifier.size(52.dp))
            }
        }

        if (showMenu) {
            @OptIn(ExperimentalMaterial3Api::class)
            ModalBottomSheet(
                onDismissRequest = { showMenu = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    // Share
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMenu = false }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Share, null, tint = onBgColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Share", color = onBgColor, fontSize = 16.sp)
                    }
                    // Download
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMenu = false }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Download, null, tint = onBgColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Download", color = onBgColor, fontSize = 16.sp)
                    }
                    // Delete
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMenu = false }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                    }
                    // Cancel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMenu = false }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Close, null, tint = onBgColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Cancel", color = onBgColor, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "History With Content")
@Composable
fun MomentHistoryScreenPreview() {
    val sampleMoments = listOf(
        MomentResponse(
            id = "1",
            imageUrl = "https://picsum.photos/1000",
            caption = "Chuyến đi Đà Lạt tuyệt vời! 🌲✨",
            capturedAt = "2024-04-26T10:00:00.000Z",
            isPublic = true
        ),
        MomentResponse(
            id = "2",
            imageUrl = "https://picsum.photos/1001",
            caption = "Cà phê sáng cùng bạn bè ☕",
            capturedAt = "2024-04-26T08:00:00.000Z",
            isPublic = true
        ),
        MomentResponse(
            id = "3",
            imageUrl = "https://picsum.photos/1002",
            caption = "Làm việc chăm chỉ nào! 💻",
            capturedAt = "2024-04-25T14:00:00.000Z",
            isPublic = true
        )
    )

    MoonPageTheme {
        MomentHistoryScreen(
            moments = sampleMoments,
            localPaths = emptyMap(),
            onNavigateToGallery = {},
            onBackToCamera = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "History Empty State")
@Composable
fun MomentHistoryScreenEmptyPreview() {
    MoonPageTheme {
        MomentHistoryScreen(
            moments = emptyList(),
            localPaths = emptyMap(),
            onNavigateToGallery = {},
            onBackToCamera = {}
        )
    }
}
