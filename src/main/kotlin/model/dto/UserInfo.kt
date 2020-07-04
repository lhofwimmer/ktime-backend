package main.kotlin.model.dto

import java.util.*

data class UserInfo(
    val uuid: UUID,
    val firstname: String?,
    val lastname: String?,
    val email: String
)