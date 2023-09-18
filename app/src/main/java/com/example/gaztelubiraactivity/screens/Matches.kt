package com.example.gaztelubiraactivity.screens

import kotlinx.serialization.Serializable

@Serializable
data class Matches(
    val id: Int,
    val home: String,
    val away: String,
    val homeGoals: Int,
    val awayGoals: Int,
    val scorers: List<String>,
    val assistants: List<String>,
    val players: List<String>,
    var isExpandable: Boolean = false
)