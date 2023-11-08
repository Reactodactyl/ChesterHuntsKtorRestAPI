package com.example.plugins.entities

data class User(
    val id :Int,
    var nickName: String,
    val password: String,
    val email: String,
    val tokenverifier: String,
    val numofStamps: Int
)
