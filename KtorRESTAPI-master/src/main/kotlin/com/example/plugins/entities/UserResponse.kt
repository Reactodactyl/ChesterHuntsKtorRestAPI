package com.example.plugins.entities

data class UserResponse(
    var id: Int,
    var nickName: String,
    var email: String,
    var access_token: String?,
    var refresh_token: String?,
    var numofStamps: Int
)
