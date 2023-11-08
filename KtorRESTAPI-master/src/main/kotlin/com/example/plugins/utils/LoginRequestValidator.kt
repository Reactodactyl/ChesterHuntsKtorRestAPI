package com.example.plugins.utils

import com.example.plugins.entities.LoginRequest
import com.example.plugins.entities.UserDraft

fun LoginRequest.isValidLogInCredentials(): Boolean {
    return  password.length in 8..255 && email.isNotEmpty() && email.isValidEmail()
}