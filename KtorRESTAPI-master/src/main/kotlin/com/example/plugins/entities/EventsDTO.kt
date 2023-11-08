package com.example.plugins.entities

data class EventsDTO(
    var titles: String,
    var subT: String,
    var details: String,
    var imData1: Int,
    var imData2: Int,
    var dates: String,//expound
    var places: String,
    var locations: String,
    var expandable: Boolean = false
)
