package com.example.gaztelubiraactivity.screens

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val goals: Int,
    val assists: Int,
    val gamesPlayed: Int,
    var proportionMatchesGoals: Double = 0.0
)



