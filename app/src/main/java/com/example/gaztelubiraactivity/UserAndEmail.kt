package com.example.gaztelubiraactivity

import kotlinx.serialization.Serializable

@Serializable
data class UserAndEmail(
    val id: Int,
    val name: String,
    val role: String,
    val email: String?
)