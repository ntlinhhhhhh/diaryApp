package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.api.DailyLogResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface DailyLogRepository {
    suspend fun createDailyLog(
        baseMoodId: RequestBody,
        date: RequestBody,
        note: RequestBody?,
        sleepHours: RequestBody?,
        isMenstruation: RequestBody?,
        menstruationPhase: RequestBody?,
        activityIds: List<MultipartBody.Part>?,
        dailyPhotos: List<MultipartBody.Part>?
    ): Result<DailyLogResponse>

    suspend fun getDailyLogByDate(date: String): Result<DailyLogResponse>
    
    suspend fun deleteDailyLog(date: String): Result<Unit>
    
    suspend fun getDailyLogsByMonth(yearMonth: String): Result<List<DailyLogResponse>>
}
