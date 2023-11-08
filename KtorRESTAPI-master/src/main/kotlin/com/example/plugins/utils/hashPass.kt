package com.example.plugins.utils

import org.mindrot.jbcrypt.BCrypt

fun String. hashPass(): String {
    return BCrypt.hashpw(this, BCrypt.gensalt())
}