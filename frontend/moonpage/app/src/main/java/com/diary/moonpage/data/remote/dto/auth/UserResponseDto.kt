package com.diary.moonpage.data.remote.dto.auth

import com.diary.moonpage.domain.model.User

data class UserResponseDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val gender: String?,
    val birthday: String?
) {
    fun toDomain(): User {
        return User(
            token = "", // Token usually managed separately or not returned here
            userId = id,
            name = name,
            avatarUrl = avatarUrl
        )
    }
}
