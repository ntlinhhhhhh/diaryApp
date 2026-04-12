package com.diary.moonpage.data.remote.dto.auth

import com.diary.moonpage.domain.model.User

data class LoginRequestDTO (
    val email: String,
    val password: String
)