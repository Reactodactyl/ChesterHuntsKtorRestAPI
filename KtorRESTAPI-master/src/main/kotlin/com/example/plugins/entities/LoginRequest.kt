package com.example.plugins.entities

data class LoginRequest (
    val email: String,
    val password: String,
    val refreshTkCheck: Boolean
)
