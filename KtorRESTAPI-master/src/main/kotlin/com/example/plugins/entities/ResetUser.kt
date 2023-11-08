package com.example.plugins.entities

import java.time.Instant

data class ResetUser (
    val resetuserid:  Int,
    val resetcode: Int,
    val datecreated: Instant
)
