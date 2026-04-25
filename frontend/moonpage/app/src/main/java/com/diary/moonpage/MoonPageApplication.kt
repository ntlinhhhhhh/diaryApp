package com.diary.moonpage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache

@HiltAndroidApp
class MoonPageApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.1) // Khoảng 10% disk space hoặc maxBytes
                    .build()
            }
            // Mặc định cho phép network cache
            .respectCacheHeaders(false) // Bỏ qua cache header từ server để bắt buộc cache
            .build()
    }
}