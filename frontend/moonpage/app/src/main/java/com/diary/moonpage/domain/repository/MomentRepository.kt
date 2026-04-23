package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.api.MomentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface MomentRepository {
    suspend fun getMyMoments(): Result<List<MomentResponse>>
    suspend fun uploadMoment(
        dailyLogId: RequestBody,
        imageFile: MultipartBody.Part,
        caption: RequestBody,
        isPublic: RequestBody,
        capturedAt: RequestBody
    ): Result<Unit>
}
