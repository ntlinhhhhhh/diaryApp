package com.diary.moonpage.presentation.screens.moment

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.diary.moonpage.core.util.ImageUtils
import com.diary.moonpage.presentation.components.moment.CaptureButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

data class MomentTag(
    val id: String,
    val icon: ImageVector?,
    val text: String,
    val containerColor: Color? = null,
    val contentColor: Color = Color.White
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MomentCameraScreen(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        MomentCameraContent(
            onNavigateToGallery = onNavigateToGallery,
            onNavigateToHistory = onNavigateToHistory
        )
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MomentCameraContent(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: MomentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var capturedLensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()
    var isSuccess by remember { mutableStateOf(false) }
    var showTagSheet by remember { mutableStateOf(false) }

    // States for Tag Details
    var userMessage by remember { mutableStateOf("") }
    var userRating by remember { mutableIntStateOf(0) }
    var userLocation by remember { mutableStateOf("") }
    var userWeather by remember { mutableStateOf("Sunny ☀️") }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_COARSE_LOCATION)
    val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

    val allTags = remember {
        listOf(
            MomentTag("text", Icons.Rounded.TextFields, "Message"),
            MomentTag("review", Icons.Rounded.Star, "Review"),
            MomentTag("location", Icons.Rounded.LocationOn, "Location"),
            MomentTag("weather", Icons.Rounded.WbSunny, "Weather"),
            MomentTag("time", Icons.Rounded.AccessTime, currentTime),
            MomentTag("party", null, "Party Time!", containerColor = Color(0xFF80FFE8), contentColor = Color.Black),
            MomentTag("ootd", null, "OOTD", containerColor = Color.White, contentColor = Color.Black),
            MomentTag("missyou", null, "Miss you", containerColor = Color(0xFFFF4B4B), contentColor = Color.White)
        )
    }

    val pagerState = rememberPagerState(pageCount = { allTags.size })

    // Reset UI after success
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            capturedImageUri = null
            userMessage = ""
            userRating = 0
            userLocation = ""
            isSuccess = false
        }
    }

    // Auto-request location when swiped or permission granted
    LaunchedEffect(pagerState.currentPage, locationPermissionState.status.isGranted, capturedImageUri) {
        if (capturedImageUri != null) {
            val currentTag = allTags[pagerState.currentPage]
            if (currentTag.id == "location" || currentTag.id == "weather") {
                if (!locationPermissionState.status.isGranted) {
                    locationPermissionState.launchPermissionRequest()
                } else if (userLocation.isEmpty()) {
                    userLocation = "Hà Nội, Việt Nam"
                }
            }
        }
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("Camera", "Use case binding failed", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(56.dp)
        ) {
            if (capturedImageUri != null) {
                Text(
                    text = "Upload",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Normal,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
                
                IconButton(
                    onClick = { 
                        Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        Icons.Rounded.FileDownload,
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .align(Alignment.CenterEnd)
                )
            }
        }

        // Image Box - Fixed 0.9f width
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (capturedImageUri != null) {
                AsyncImage(
                    model = capturedImageUri,
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = if (capturedLensFacing == CameraSelector.LENS_FACING_FRONT) -1f else 1f),
                    contentScale = ContentScale.Crop
                )
                
                // Swipeable Tag Overlay - Positioned low
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 4.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentPadding = PaddingValues(horizontal = 48.dp)
                    ) { page ->
                        val tag = allTags[page]
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = tag.containerColor ?: Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (tag.id != "review") {
                                        tag.icon?.let {
                                            Icon(
                                                it,
                                                null,
                                                tint = tag.contentColor,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clickable { showTagSheet = true }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                    }

                                    when(tag.id) {
                                        "text" -> {
                                            BasicTextField(
                                                value = userMessage,
                                                onValueChange = { userMessage = it },
                                                textStyle = TextStyle(
                                                    color = tag.contentColor,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp,
                                                    textAlign = TextAlign.Center
                                                ),
                                                cursorBrush = SolidColor(tag.contentColor),
                                                decorationBox = { innerTextField ->
                                                    if (userMessage.isEmpty()) {
                                                        Text(
                                                            "Add a message",
                                                            color = tag.contentColor.copy(alpha = 0.6f),
                                                            fontSize = 16.sp
                                                        )
                                                    }
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
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clickable { userRating = starRating }
                                                    )
                                                }
                                            }
                                        }
                                        "location" -> {
                                            Text(
                                                text = if (userLocation.isNotEmpty()) userLocation else "Tap to add location",
                                                color = tag.contentColor,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.clickable {
                                                    if (locationPermissionState.status.isGranted) {
                                                        userLocation = "Hà Nội, Việt Nam"
                                                    } else {
                                                        locationPermissionState.launchPermissionRequest()
                                                    }
                                                }
                                            )
                                        }
                                        "weather" -> {
                                            val weathers = listOf("Sunny ☀️", "Cloudy ☁️", "Rainy 🌧️", "Snowy ❄️")
                                            Text(
                                                text = userWeather,
                                                color = tag.contentColor,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.clickable {
                                                    if (locationPermissionState.status.isGranted) {
                                                        val currentIndex = weathers.indexOf(userWeather)
                                                        userWeather = weathers[(currentIndex + 1) % weathers.size]
                                                    } else {
                                                        locationPermissionState.launchPermissionRequest()
                                                    }
                                                }
                                            )
                                        }
                                        else -> {
                                            Text(
                                                text = tag.text,
                                                color = tag.contentColor,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Pager dots sync with current page
        if (capturedImageUri != null) {
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(allTags.size) { i ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == pagerState.currentPage) MaterialTheme.colorScheme.onBackground 
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons - NO CAPTIONS, NO BORDERS, CONSISTENT SIZE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (capturedImageUri == null) {
                // Camera Mode
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNavigateToGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PhotoLibrary, null, tint = MaterialTheme.colorScheme.primary)
                }

                CaptureButton(
                    onClick = {
                        takePhoto(
                            context = context,
                            imageCapture = imageCapture,
                            executor = ContextCompat.getMainExecutor(context),
                            onImageCaptured = { uri -> 
                                capturedImageUri = uri 
                                capturedLensFacing = lensFacing
                            },
                            onError = { Log.e("Camera", "Capture failed", it) }
                        )
                    }
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.FlipCameraIos, null, tint = MaterialTheme.colorScheme.primary)
                }
            } else {
                // Send Mode
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { 
                            capturedImageUri = null
                            userMessage = ""
                            userRating = 0
                            userLocation = ""
                            userWeather = "Sunny ☀️"
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Send Button with Animation
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .clickable(enabled = !isLoading && !isSuccess) { 
                            capturedImageUri?.let { uri ->
                                scope.launch {
                                    val compressedFile = ImageUtils.compressAndCropSquare(context, uri)
                                    if (compressedFile != null) {
                                        val currentTag = allTags[pagerState.currentPage]
                                        val caption = when(currentTag.id) {
                                            "text" -> userMessage
                                            "review" -> "Rating: $userRating stars"
                                            "location" -> userLocation
                                            "weather" -> userWeather
                                            else -> currentTag.text
                                        }
                                        viewModel.uploadMoment(compressedFile, caption)
                                        isSuccess = true
                                    } else {
                                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = isSuccess,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "SuccessAnimation"
                    ) { success ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (success) Icons.Default.Check else Icons.Rounded.Send,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showTagSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        "Tags",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (capturedImageUri == null) {
            Row(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateToHistory() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.History,
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "HISTORY",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Spacer(modifier = Modifier.height(50.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showTagSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTagSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    "Captions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                FlowRow(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allTags.forEachIndexed { index, tag ->
                        TagChip(tag) {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            showTagSheet = false
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TagChip(tag: MomentTag, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = tag.containerColor ?: MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tag.icon?.let {
                Icon(it, null, tint = tag.contentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                tag.text,
                color = tag.contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())
    val file = File(context.cacheDir, "${name}.jpg")
    
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = Uri.fromFile(file)
                onImageCaptured(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
