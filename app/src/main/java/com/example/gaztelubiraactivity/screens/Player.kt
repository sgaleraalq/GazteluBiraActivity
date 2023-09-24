package com.example.gaztelubiraactivity.screens

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val goals: Int,
    val assists: Int,
    val gamesPlayed: Int,
    val mvp: Int,
    var proportionMatchesGoals: Double = 0.0
)



