package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.api.MomentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface MomentRepository {
    suspend fun getMyMoments(): Result<List<MomentResponse>>
    suspend fun getMoment(id: String): Result<MomentResponse>
    suspend fun deleteMoment(id: String): Result<Unit>
    suspend fun uploadMoment(
        dailyLogId: RequestBody,
        imageFile: MultipartBody.Part,
        caption: RequestBody,
        isPublic: RequestBody,
        capturedAt: RequestBody,
        location: RequestBody?,
        weather: RequestBody?,
        rating: RequestBody?
    ): Result<MomentResponse>
}
