package com.example.plugins.entities

data class Stamp(
    val idstamps: Int,
    val photo: String,
    val title: String,
    val description: String,
    val hintText: String,
    val coordinateX: Int,
    val coordinateY: Int,
    val status:Boolean
)