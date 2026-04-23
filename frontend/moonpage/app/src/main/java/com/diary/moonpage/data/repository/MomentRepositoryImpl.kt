package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.MomentApi
import com.diary.moonpage.data.remote.api.MomentResponse
import com.diary.moonpage.domain.repository.MomentRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MomentRepositoryImpl @Inject constructor(
    private val api: MomentApi
) : MomentRepository {
    override suspend fun getMyMoments(): Result<List<MomentResponse>> {
        return try {
            val response = api.getMyMoments()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch moments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadMoment(
        dailyLogId: RequestBody,
        imageFile: MultipartBody.Part,
        caption: RequestBody,
        isPublic: RequestBody,
        capturedAt: RequestBody
    ): Result<Unit> {
        return try {
            val response = api.uploadMoment(dailyLogId, imageFile, caption, isPublic, capturedAt)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
