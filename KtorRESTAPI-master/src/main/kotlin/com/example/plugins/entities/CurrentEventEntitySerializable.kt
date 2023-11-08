package com.example.plugins.entities

import java.time.Instant
import java.util.*

data class CurrentEventEntitySerializable(
    val idcurrentevent: Int,
    val startTime: Date,
    val eventHost: String,
    val locationAddress: String,
    val Title: String,
    val subTitle: String,
    val details: String,
    val image1:String,
    val image2:String,
    val duration:Int,
    val expandable: Boolean

)
