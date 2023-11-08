package com.example.plugins.utils

import com.example.plugins.entities.UserDraft

fun UserDraft.isValidCredentials(): Boolean {
    return nickName.length in 5..15 && password.length in 8..255 && email.isNotEmpty() && email.isValidEmail()
}