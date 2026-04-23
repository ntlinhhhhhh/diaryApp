package com.diary.moonpage.presentation.components.moment

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlipCameraIos
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.diary.moonpage.presentation.theme.MoonPageTheme
import java.io.File

@Composable
fun CameraMainUI(
    onNavigateToGallery: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onImageCaptured: (Uri, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lensFacing) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            Log.e("Camera", "Use case binding failed", e)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).height(56.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .align(Alignment.CenterEnd)
            )
        }

        // Image Box - Fixed 0.9f width, 1f aspect ratio
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
                    .clickable { onNavigateToGallery() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.PhotoLibrary, null, tint = MaterialTheme.colorScheme.primary)
            }

            CaptureButton(onClick = {
                takePhoto(context, imageCapture, ContextCompat.getMainExecutor(context), onImageCaptured = { uri ->
                    onImageCaptured(uri, lensFacing)
                }, onError = { Log.e("Camera", "Capture failed", it) })
            })

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), CircleShape)
                    .clickable {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) 
                            CameraSelector.LENS_FACING_FRONT 
                        else 
                            CameraSelector.LENS_FACING_BACK
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.FlipCameraIos, null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onNavigateToHistory() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.History, "History", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("HISTORY", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: java.util.concurrent.Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            onImageCaptured(Uri.fromFile(file))
        }
        override fun onError(exception: ImageCaptureException) {
            onError(exception)
        }
    })
}

@ComposePreview(showBackground = true)
@Composable
fun CameraMainUIPreview() {
    MoonPageTheme {
        CameraMainUI(
            onNavigateToGallery = {},
            onNavigateToHistory = {},
            onImageCaptured = { _, _ -> }
        )
    }
}
