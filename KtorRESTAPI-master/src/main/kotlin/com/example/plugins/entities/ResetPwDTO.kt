package com.example.plugins.entities

data class ResetPwDTO(
    val email: String ,
    val resetCode: Int
)