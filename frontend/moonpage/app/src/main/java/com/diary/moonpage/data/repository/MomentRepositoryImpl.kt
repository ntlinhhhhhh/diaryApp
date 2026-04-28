package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.MomentApi
import com.diary.moonpage.domain.model.Moment
import com.diary.moonpage.domain.repository.MomentRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class MomentRepositoryImpl @Inject constructor(
    private val api: MomentApi
) : MomentRepository {
    override suspend fun getMyMoments(): Result<List<Moment>> {
        return try {
            val response = api.getMyMoments()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Failed to fetch moments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMoment(id: String): Result<Moment> {
        return try {
            val response = api.getMoment(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Failed to fetch moment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMoment(id: String): Result<Unit> {
        return try {
            val response = api.deleteMoment(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete moment"))
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
        capturedAt: RequestBody,
        location: RequestBody?,
        weather: RequestBody?,
        rating: RequestBody?
    ): Result<Moment> {
        return try {
            val response = api.uploadMoment(dailyLogId, imageFile, caption, isPublic, capturedAt, location, weather, rating)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Upload failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
