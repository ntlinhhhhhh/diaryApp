package com.diary.moonpage.presentation.screens.moment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraIos
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.moonpage.presentation.components.moment.CameraActionButton
import com.diary.moonpage.presentation.components.moment.CaptureButton
import com.diary.moonpage.presentation.theme.MoonPageTheme

@Composable
fun MomentCameraScreen(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onFlipCamera: () -> Unit,
    onCapture: () -> Unit
) {
    MomentCameraContent(
        onNavigateToGallery = onNavigateToGallery,
        onNavigateToHistory = onNavigateToHistory,
        onFlipCamera = onFlipCamera,
        onCapture = onCapture
    )
}

@Composable
fun MomentCameraContent(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onFlipCamera: () -> Unit,
    onCapture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // Profile Picture Placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        // Camera Preview Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "Camera Preview",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CameraActionButton(
                icon = Icons.Default.PhotoLibrary,
                label = "Gallery",
                onClick = onNavigateToGallery
            )

            CaptureButton(
                onClick = onCapture
            )

            CameraActionButton(
                icon = Icons.Default.FlipCameraIos,
                label = "Flip",
                onClick = onFlipCamera
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // History Button
        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .clickable { onNavigateToHistory() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "HISTORY",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MomentCameraScreenPreview() {
    MoonPageTheme() {
        MomentCameraScreen({}, {}, {}, {})
    }
}
