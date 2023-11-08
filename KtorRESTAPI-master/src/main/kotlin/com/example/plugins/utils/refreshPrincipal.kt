package com.example.plugins.utils

import io.ktor.auth.*

data class refreshPrincipal(val id: Int, val type: String, val key: String) : Principal {
}