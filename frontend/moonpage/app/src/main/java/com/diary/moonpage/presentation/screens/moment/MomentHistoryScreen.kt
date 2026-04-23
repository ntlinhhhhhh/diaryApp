package com.diary.moonpage.presentation.screens.moment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.presentation.components.moment.MomentFeedItem
import com.diary.moonpage.presentation.theme.MoonPageTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentHistoryScreen(
    moments: List<MomentResponse>,
    onNavigateToGallery: () -> Unit,
    onBackToCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sortedMoments = remember(moments) { moments.sortedByDescending { it.capturedAt } }
    val feedPagerState = rememberPagerState(pageCount = { sortedMoments.size })
    val onBgColor = MaterialTheme.colorScheme.onBackground
    val bgColor = MaterialTheme.colorScheme.background

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
                modifier = Modifier.fillMaxSize()
            ) { index ->
                MomentFeedItem(sortedMoments[index])
            }
        }

        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.15f))
                    .align(Alignment.CenterStart)
            )

            Surface(
                color = onBgColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("History", color = onBgColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = onBgColor, modifier = Modifier.size(22.dp))
                }
            }

            IconButton(
                onClick = { /* Dummy */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Rounded.ChatBubbleOutline, null, tint = onBgColor, modifier = Modifier.size(30.dp))
            }
        }

        // Bottom Bar
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
                    .border(5.dp, onBgColor.copy(alpha = 0.35f), CircleShape)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(onBgColor)
                    .clickable { onBackToCamera() }
            )

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(onBgColor.copy(alpha = 0.1f))
                    .clickable { /* Menu logic */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MoreHoriz, null, tint = onBgColor, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MomentHistoryScreenPreview() {
    MoonPageTheme {
        MomentHistoryScreen(
            moments = emptyList(),
            onNavigateToGallery = {},
            onBackToCamera = {}
        )
    }
}
