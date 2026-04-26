package com.diary.moonpage.presentation.screens.moment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.presentation.components.moment.CameraMainUI
import com.diary.moonpage.presentation.components.moment.MomentTag
import com.diary.moonpage.presentation.components.moment.TagChip
import com.diary.moonpage.presentation.theme.MoonPageTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MomentCameraScreen(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: MomentViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    LaunchedEffect(Unit) {
        cameraPermissionState.launchMultiplePermissionRequest()
    }

    val cameraPermission = cameraPermissionState.permissions.find { it.permission == Manifest.permission.CAMERA }
    if (cameraPermission?.status?.isGranted == true) {
        val moments by viewModel.moments.collectAsState()
        val localPaths by viewModel.localPaths.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val context = LocalContext.current
        
        MomentCameraContent(
            moments = moments,
            localPaths = localPaths,
            isLoading = isLoading,
            allTags = viewModel.allTags,
            locationPermissionState = locationPermissionState,
            onUpload = { file, caption, location, weather, rating, onSuccess -> 
                viewModel.uploadMoment(file, caption, location, weather, rating, onSuccess = onSuccess)
            },
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToHistory = onNavigateToHistory,
            onShare = { viewModel.shareMoment(context, it.imageUrl) },
            onDownload = { viewModel.downloadMoment(it.imageUrl) },
            onDelete = { viewModel.deleteMoment(it.id) }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MomentCameraContent(
    moments: List<MomentResponse>,
    localPaths: Map<String, String>,
    isLoading: Boolean,
    allTags: List<MomentTag>,
    locationPermissionState: com.google.accompanist.permissions.MultiplePermissionsState,
    onUpload: (File, String, String?, String?, Float?, () -> Unit) -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onShare: (MomentResponse) -> Unit,
    onDownload: (MomentResponse) -> Unit,
    onDelete: (MomentResponse) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedLensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    
    val verticalPagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            capturedImageUri = uri
            capturedLensFacing = CameraSelector.LENS_FACING_BACK
        }
    }

    var userMessage by remember { mutableStateOf("") }
    var userRating by remember { mutableFloatStateOf(0.0f) }
    var userLocation by remember { mutableStateOf("") }
    var userWeather by remember { mutableStateOf("Sunny ☀️") }
    var isSuccess by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }

    val uploadPagerState = rememberPagerState(pageCount = { allTags.size })

    val weatherIcons = listOf("Sunny ☀️", "Cloudy ☁️", "Rainy 🌧️", "Snowy ❄️", "Windy 💨")

    // Hàm helper để xử lý tọa độ thành địa chỉ dạng "Quận/Thành Phố"
    val reverseGeocode = { location: Location ->
        scope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val district = address.subLocality
                        ?: address.locality
                        ?: address.subAdminArea
                    val city = address.adminArea ?: address.locality ?: "Unknown"
                    val locationName = if (district != null && district != city) {
                        "$district/$city"
                    } else {
                        city
                    }
                    withContext(Dispatchers.Main) {
                        userLocation = locationName
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    userLocation = "Location error"
                }
            }
        }
    }

    // Helper: kiểm tra GPS có được bật không
    fun isGpsEnabled(ctx: android.content.Context): Boolean {
        val lm = ctx.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    var showGpsDialog by remember { mutableStateOf(false) }

    // GPS enable dialog
    if (showGpsDialog) {
        AlertDialog(
            onDismissRequest = { showGpsDialog = false },
            title = { Text("Location Services Off") },
            text = { Text("Please enable Location Services to add your location.") },
            confirmButton = {
                TextButton(onClick = {
                    showGpsDialog = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showGpsDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Chiến thuật Hybrid: Thử lastLocation trước, nếu hụt mới getCurrentLocation
    val fetchLocationFast = {
        if (locationPermissionState.allPermissionsGranted) {
            if (!isGpsEnabled(context)) {
                // GPS tắt – hiện dialog yêu cầu bật
                showGpsDialog = true
            } else if (userLocation.isEmpty() || userLocation == "Location error") {
                userLocation = "Locating..."
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        reverseGeocode(loc)
                    } else {
                        // Backup bằng getCurrentLocation với độ chính xác cân bằng để lấy nhanh
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        ).addOnSuccessListener { freshLoc ->
                            if (freshLoc != null) reverseGeocode(freshLoc)
                            else userLocation = ""
                        }.addOnFailureListener {
                            userLocation = ""
                        }
                    }
                }
            }
        } else {
            locationPermissionState.launchMultiplePermissionRequest()
        }
    }

    // pendingLocationRequest: được bật lên khi user vào tag location nhưng chưa có quyền
    // Khi quyền được cấp xong mới tự động fetch
    var pendingLocationRequest by remember { mutableStateOf(false) }

    LaunchedEffect(locationPermissionState.allPermissionsGranted) {
        if (locationPermissionState.allPermissionsGranted && pendingLocationRequest) {
            pendingLocationRequest = false
            if (!isGpsEnabled(context)) showGpsDialog = true
            else fetchLocationFast()
        }
    }

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
                            Log.d("MomentCamera", "Captured image: $uri")
                            capturedImageUri = uri
                            capturedLensFacing = lensFacing
                        }
                    )
                } else {
                    MomentHistoryScreen(
                        moments = moments,
                        localPaths = localPaths,
                        onNavigateToGallery = onNavigateToGallery,
                        onBackToCamera = {
                            scope.launch { verticalPagerState.animateScrollToPage(0) }
                        },
                        onShare = onShare,
                        onDownload = onDownload,
                        onDelete = onDelete
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
                onLocationClick = {
                    // Chỉ xử lý location khi user chủ động vào tag location
                    if (!locationPermissionState.allPermissionsGranted) {
                        // Chưa có quyền – đánh dấu pending rồi hỏi quyền
                        pendingLocationRequest = true
                        locationPermissionState.launchMultiplePermissionRequest()
                    } else if (!isGpsEnabled(context)) {
                        // Có quyền nhưng GPS tắt – hiện dialog
                        showGpsDialog = true
                    } else {
                        fetchLocationFast()
                    }
                },
                userWeather = userWeather,
                onWeatherClick = {
                    val currentIndex = weatherIcons.indexOf(userWeather)
                    userWeather = weatherIcons[(currentIndex + 1) % weatherIcons.size]
                },
                isLoading = isLoading,
                isSuccess = isSuccess,
                onCancel = { capturedImageUri = null },
                onUpload = { file, caption ->
                    val currentTag = allTags[uploadPagerState.currentPage]
                    val ratingToUpload = if (currentTag.id == "review") userRating else null
                    val locationToUpload = if (currentTag.id == "location") userLocation else null
                    val weatherToUpload = if (currentTag.id == "weather") userWeather else null

                    onUpload(file, caption, locationToUpload, weatherToUpload, ratingToUpload) {
                        isSuccess = true
                    }
                },
                onShowTagSheet = { showTagSheet = true }
            )

            if (isSuccess) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(500)
                    capturedImageUri = null
                    isSuccess = false
                    userMessage = ""
                    userRating = 0.0f
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
