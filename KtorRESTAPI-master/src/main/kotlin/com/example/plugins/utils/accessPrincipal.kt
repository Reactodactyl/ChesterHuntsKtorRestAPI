package com.example.plugins.utils

import io.ktor.auth.*


data class accessPrincipal(val id: Int, val type: String) : Principal {

}