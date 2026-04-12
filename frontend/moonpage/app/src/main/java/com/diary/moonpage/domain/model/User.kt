package com.diary.moonpage.domain.model

data class User(
    val token: String,
    val userId: String,
    val name: String,
    val avatarUrl: String?
)
