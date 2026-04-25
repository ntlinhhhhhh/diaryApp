package com.diary.moonpage.data.repository

import com.diary.moonpage.data.remote.api.DailyLogApi
import com.diary.moonpage.data.remote.api.DailyLogResponse
import com.diary.moonpage.domain.repository.DailyLogRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class DailyLogRepositoryImpl @Inject constructor(
    private val api: DailyLogApi
) : DailyLogRepository {

    override suspend fun createDailyLog(
        baseMoodId: RequestBody,
        date: RequestBody,
        note: RequestBody?,
        sleepHours: RequestBody?,
        isMenstruation: RequestBody?,
        menstruationPhase: RequestBody?,
        activityIds: List<MultipartBody.Part>?,
        dailyPhotos: List<MultipartBody.Part>?
    ): Result<DailyLogResponse> {
        return try {
            val response = api.createDailyLog(
                baseMoodId, date, note, sleepHours, isMenstruation, menstruationPhase, activityIds, dailyPhotos
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create DailyLog: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyLogByDate(date: String): Result<DailyLogResponse> {
        return try {
            val response = api.getDailyLogByDate(date)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get DailyLog for date $date: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDailyLog(date: String): Result<Unit> {
        return try {
            val response = api.deleteDailyLog(date)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete DailyLog: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDailyLogsByMonth(yearMonth: String): Result<List<DailyLogResponse>> {
        return try {
            val response = api.getDailyLogsByMonth(yearMonth)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get DailyLogs for month $yearMonth: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
