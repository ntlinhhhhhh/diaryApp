package com.diary.moonpage.core.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.math.min

object ImageUtils {
    fun compressAndCropSquare(context: Context, uri: Uri, quality: Int = 80): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Fix rotation
            val rotation = getRotation(context, uri)
            val rotatedBitmap = if (rotation != 0) {
                val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            } else {
                originalBitmap
            }

            // Crop to square
            val size = min(rotatedBitmap.width, rotatedBitmap.height)
            val x = (rotatedBitmap.width - size) / 2
            val y = (rotatedBitmap.height - size) / 2
            val squareBitmap = Bitmap.createBitmap(rotatedBitmap, x, y, size, size)

            // Scale down if too large (e.g., max 1080px)
            val finalBitmap = if (size > 1080) {
                Bitmap.createScaledBitmap(squareBitmap, 1080, 1080, true)
            } else {
                squareBitmap
            }

            // Save to temp file
            val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(compressedFile)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()

            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getRotation(context: Context, uri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            inputStream.close()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun downloadAndSaveImage(context: Context, imageUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                saveBitmapToGallery(context, bitmap)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "MP_${System.currentTimeMillis()}.jpg"
        val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MoonPage")
            }
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val file = File(imagesDir, filename)
            FileOutputStream(file)
        }

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}
