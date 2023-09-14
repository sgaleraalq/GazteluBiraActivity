package com.example.gaztelubiraactivity.screens

import com.google.gson.annotations.SerializedName

data class Matches(
    @SerializedName("home")
    val local: String,
    @SerializedName("away")
    val visitor: String,
    @SerializedName("homeGoals")
    val localGoals: Int,
    @SerializedName("awayGoals")
    val visitorGoals: Int,
    val matchesStats: MatchesStats,
    var isExpandable: Boolean = false
)