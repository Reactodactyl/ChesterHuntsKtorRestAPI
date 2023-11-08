package com.example.plugins.entities

import com.example.plugins.utils.isValidEmail
import org.mindrot.jbcrypt.BCrypt
import java.util.regex.Matcher
import java.util.regex.Pattern

data class UserDraft(
    val nickName: String,
    val password: String,
    val email: String
)