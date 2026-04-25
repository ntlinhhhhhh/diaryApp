package com.diary.moonpage.data.remote.api

import com.google.gson.annotations.SerializedName

data class DailyLogResponse(
    @SerializedName("id") val id: String,
    @SerializedName("baseMoodId") val baseMoodId: Int,
    @SerializedName("date") val date: String,
    @SerializedName("note") val note: String?,
    @SerializedName("sleepHours") val sleepHours: Double?,
    @SerializedName("isMenstruation") val isMenstruation: Boolean,
    @SerializedName("menstruationPhase") val menstruationPhase: String?,
    @SerializedName("dailyPhotos") val dailyPhotos: List<String>?,
    @SerializedName("activityIds") val activityIds: List<Int>?
)

// Use RequestBody and Multipart parts for the POST request
