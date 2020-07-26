package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.Schedule
import main.kotlin.model.db.Schedules
import main.kotlin.model.db.User
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.ScheduleResponse
import main.kotlin.net.Registry
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class ScheduleService {
    fun create(
        employee: Employee,
        scheduleStart: DateTime,
        scheduleEnd: DateTime
    ): ScheduleResponse {
        val schedule = transaction {
            Schedule.new {
                this.employee = employee
                this.scheduleStart = scheduleStart
                this.scheduleEnd = scheduleEnd
            }
        }

        return schedule.toDataClass()
    }

    fun getSchedules(user: User, employeeUUID: String?, limit: Int, offset: Long): List<ScheduleResponse> {
        if (employeeUUID == null) return getAllSchedules(limit, offset)
        val employee = Registry.employeeService.findByUUID(user, employeeUUID) ?: return getAllSchedules(limit, offset)

        return transaction {
            Schedules.select {
                Schedules.employee eq employee.id
            }.limit(limit, offset = offset)
        }.map { row ->
            val rowEmployee = Employee.findById(row[Schedules.employee]) ?: return listOf()

            ScheduleResponse(
                row[Schedules.uuid].toString(),
                row[Schedules.scheduleStart].millis,
                row[Schedules.scheduleEnd].millis,
                rowEmployee.toDataClass()
            )
        }
    }

    fun getAllSchedules(limit: Int, offset: Long): List<ScheduleResponse> {
        return Schedule.all().limit(limit, offset).map(Schedule::toDataClass)
    }
}