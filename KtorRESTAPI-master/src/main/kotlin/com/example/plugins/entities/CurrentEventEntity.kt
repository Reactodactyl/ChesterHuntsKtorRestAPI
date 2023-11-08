package com.example.plugins.entities

import java.time.Instant

data class CurrentEventEntity(
    val idcurrentevent: Int,
    val startTime: Instant,
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