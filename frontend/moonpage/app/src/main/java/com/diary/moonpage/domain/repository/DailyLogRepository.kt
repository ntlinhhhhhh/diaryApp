package com.diary.moonpage.domain.repository

import com.diary.moonpage.data.remote.api.DailyLogResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface DailyLogRepository {
    suspend fun createDailyLog(
        dateStr: String,
        baseMoodId: RequestBody,
        date: RequestBody,
        note: RequestBody?,
        sleepHours: RequestBody?,
        isMenstruation: RequestBody?,
        menstruationPhase: RequestBody?,
        activityIds: List<MultipartBody.Part>?,
        dailyPhotos: List<MultipartBody.Part>?
    ): Result<Unit>

    suspend fun getDailyLogByDate(date: String): Result<DailyLogResponse>
    
    suspend fun deleteDailyLog(date: String): Result<Unit>
    
    fun getDailyLogsByMonth(yearMonth: String): kotlinx.coroutines.flow.Flow<List<DailyLogResponse>>
}
