package com.diary.moonpage.domain.usecase.moment

import com.diary.moonpage.domain.model.Moment
import com.diary.moonpage.domain.repository.MomentRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UploadMomentUseCase @Inject constructor(
    private val repository: MomentRepository
) {
    suspend operator fun invoke(
        dailyLogId: RequestBody,
        imageFile: MultipartBody.Part,
        caption: RequestBody,
        isPublic: RequestBody,
        capturedAt: RequestBody,
        location: RequestBody?,
        weather: RequestBody?,
        rating: RequestBody?
    ): Result<Moment> {
        return repository.uploadMoment(
            dailyLogId, imageFile, caption, isPublic, capturedAt, location, weather, rating
        )
    }
}
