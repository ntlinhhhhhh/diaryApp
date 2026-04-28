package com.diary.moonpage.presentation.screens.moment

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MomentDetailRoute(
    momentId: String,
    viewModel: MomentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MomentHistoryScreen(
        moments = uiState.moments,
        localPaths = uiState.localPaths,
        initialMomentId = momentId,
        onNavigateToGallery = onNavigateToGallery,
        onBackToCamera = onNavigateBack,
        onShare = { moment -> viewModel.onEvent(MomentUiEvent.ShareMoment(moment.imageUrl)) },
        onDownload = { moment -> viewModel.onEvent(MomentUiEvent.DownloadMoment(moment.imageUrl)) },
        onDelete = { moment -> viewModel.onEvent(MomentUiEvent.DeleteMoment(moment.id)) }
    )
}
