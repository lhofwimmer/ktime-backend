package main.kotlin.model.dto

data class ScheduleRequest(
    val scheduleStart: Long?,
    val scheduleEnd: Long?,
    val employee: String
)

data class ScheduleResponse(
    val uuid: String,
    val scheduleStart: Long?,
    val scheduleEnd: Long?,
    val employee: EmployeeResponse
)