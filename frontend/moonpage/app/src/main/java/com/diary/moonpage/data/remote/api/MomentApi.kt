package com.diary.moonpage.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MomentApi {
    @Multipart
    @POST("api/moments")
    suspend fun uploadMoment(
        @Part("DailyLogId") dailyLogId: RequestBody,
        @Part imageFile: MultipartBody.Part,
        @Part("Caption") caption: RequestBody,
        @Part("IsPublic") isPublic: RequestBody,
        @Part("CapturedAt") capturedAt: RequestBody,
        @Part("Location") location: RequestBody?,
        @Part("Weather") weather: RequestBody?,
        @Part("Rating") rating: RequestBody?
    ): Response<MomentResponse>

    @GET("api/moments/me")
    suspend fun getMyMoments(): Response<List<MomentResponse>>
}

data class MomentResponse(
    val id: String,
    val imageUrl: String,
    val caption: String?,
    val capturedAt: String,
    val isPublic: Boolean,
    val location: String? = null,
    val weather: String? = null,
    val rating: Float? = null
)
