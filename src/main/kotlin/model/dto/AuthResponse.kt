package main.kotlin.model.dto

data class AuthResponse(
    val token: String?,
    val success: Boolean
)