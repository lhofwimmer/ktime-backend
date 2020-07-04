package main.kotlin.model.dto

import java.math.BigDecimal

data class EmployeeResponse(
    val uuid: String,
    val weeklyHours: BigDecimal?,
    val entryDate: Long?,
    val firstname: String,
    val lastname: String,
    val employer: String
)

data class EmployeeRequest(
    val firstname: String,
    val lastname: String,
    val weeklyHours: BigDecimal?,
    val entryDate: Long?
)