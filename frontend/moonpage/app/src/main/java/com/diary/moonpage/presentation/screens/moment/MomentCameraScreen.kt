package com.diary.moonpage.presentation.screens.moment

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.presentation.components.moment.CameraMainUI
import com.diary.moonpage.presentation.components.moment.MomentTag
import com.diary.moonpage.presentation.components.moment.TagChip
import com.diary.moonpage.presentation.theme.MoonPageTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MomentCameraScreen(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: MomentViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        val moments by viewModel.moments.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        
        MomentCameraContent(
            moments = moments,
            isLoading = isLoading,
            allTags = viewModel.allTags,
            onUpload = viewModel::uploadMoment,
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToHistory = onNavigateToHistory
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentCameraContent(
    moments: List<MomentResponse>,
    isLoading: Boolean,
    allTags: List<MomentTag>,
    onUpload: (File, String) -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedLensFacing by remember { mutableIntStateOf(0) }
    
    // Page 0: Camera, Page 1: History. Swipe down to see History.
    val verticalPagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            capturedImageUri = uri
        }
    }

    // Tag States
    var userMessage by remember { mutableStateOf("") }
    var userRating by remember { mutableIntStateOf(0) }
    var userLocation by remember { mutableStateOf("") }
    var userWeather by remember { mutableStateOf("Sunny ☀️") }
    var isSuccess by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }

    val uploadPagerState = rememberPagerState(pageCount = { allTags.size })

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedImageUri == null) {
            VerticalPager(
                state = verticalPagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                if (page == 0) {
                    CameraMainUI(
                        onSelectFromGallery = { galleryLauncher.launch("image/*") },
                        onNavigateToHistory = {
                            scope.launch { verticalPagerState.animateScrollToPage(1) }
                        },
                        onImageCaptured = { uri, lensFacing ->
                            capturedImageUri = uri
                            capturedLensFacing = lensFacing
                        }
                    )
                } else {
                    MomentHistoryScreen(
                        moments = moments,
                        onNavigateToGallery = onNavigateToGallery,
                        onBackToCamera = {
                            scope.launch { verticalPagerState.animateScrollToPage(0) }
                        }
                    )
                }
            }
        } else {
            MomentUploadScreen(
                capturedImageUri = capturedImageUri!!,
                capturedLensFacing = capturedLensFacing,
                pagerState = uploadPagerState,
                allTags = allTags,
                userMessage = userMessage,
                onUserMessageChange = { userMessage = it },
                userRating = userRating,
                onUserRatingChange = { userRating = it },
                userLocation = userLocation,
                onLocationClick = { /* Click handled here if needed */ },
                userWeather = userWeather,
                onWeatherClick = { /* Cycle weather */ },
                isLoading = isLoading,
                isSuccess = isSuccess,
                onCancel = { capturedImageUri = null },
                onUpload = { file, caption ->
                    onUpload(file, caption)
                    isSuccess = true
                },
                onShowTagSheet = { showTagSheet = true }
            )

            if (isSuccess) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    capturedImageUri = null
                    isSuccess = false
                    userMessage = ""
                    userRating = 0
                    userLocation = ""
                }
            }
        }

        if (showTagSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTagSheet = false }
            ) {
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                ) {
                    allTags.forEachIndexed { index, tag ->
                        TagChip(tag) {
                            scope.launch { uploadPagerState.animateScrollToPage(index) }
                            showTagSheet = false
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MomentCameraScreenPreview() {
    val mockTags = listOf(
        MomentTag("text", Icons.Rounded.TextFields, "Message"),
        MomentTag("review", Icons.Rounded.Star, "Review"),
        MomentTag("location", Icons.Rounded.LocationOn, "Location"),
        MomentTag("weather", Icons.Rounded.WbSunny, "Weather")
    )

    MoonPageTheme {
        MomentCameraContent(
            moments = emptyList(),
            isLoading = false,
            allTags = mockTags,
            onUpload = { _, _ -> },
            onNavigateToGallery = {},
            onNavigateToHistory = {}
        )
    }
}
