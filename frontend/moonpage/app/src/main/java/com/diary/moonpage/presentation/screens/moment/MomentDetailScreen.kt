package com.diary.moonpage.presentation.screens.moment

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Wrapper screen that opens MomentHistoryScreen scrolled to the specific moment
 * identified by [momentId]. Navigated to from GalleryScreen when a photo is tapped.
 */
@Composable
fun MomentDetailScreen(
    momentId: String,
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit,
    viewModel: MomentViewModel = hiltViewModel()
) {
    val moments by viewModel.moments.collectAsState()
    val localPaths by viewModel.localPaths.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Sort the same way MomentHistoryScreen does, find the initial page index
    val sortedMoments = remember(moments) {
        moments.sortedByDescending { it.capturedAt }
    }

    MomentHistoryScreen(
        moments = sortedMoments,
        localPaths = localPaths,
        initialMomentId = momentId,
        onNavigateToGallery = onNavigateToGallery,
        onBackToCamera = onNavigateBack,
        onShare = { viewModel.shareMoment(context, it.imageUrl) },
        onDownload = { viewModel.downloadMoment(it.imageUrl) },
        onDelete = { viewModel.deleteMoment(it.id) }
    )
}
