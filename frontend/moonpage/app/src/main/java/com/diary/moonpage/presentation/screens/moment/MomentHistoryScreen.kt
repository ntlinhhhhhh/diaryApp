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
import coil.imageLoader
import coil.request.ImageRequest
import com.diary.moonpage.core.theme.MoonPageTheme
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.presentation.components.moment.MomentFeedItem

@Composable
fun MomentHistoryScreen(
    moments: List<MomentResponse>,
    localPaths: Map<String, String>, // Thêm mapping đường dẫn local
    onNavigateToGallery: () -> Unit,
    onBackToCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedMoments = remember(moments) { moments.sortedByDescending { it.capturedAt } }
    val feedPagerState = rememberPagerState(pageCount = { sortedMoments.size })
    val onBgColor = MaterialTheme.colorScheme.onBackground
    val bgColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current

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

        // Top Header (Giữ nguyên giao diện cũ)
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

        // Bottom Bar (Giữ nguyên giao diện cũ)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 40.dp, vertical = 40.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.2f))
                    .clickable { onBackToCamera() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(onBgColor)
                )
            }

            Spacer(modifier = Modifier.size(52.dp))
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
