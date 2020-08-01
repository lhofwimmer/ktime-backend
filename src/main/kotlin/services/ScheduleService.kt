package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.Employees
import main.kotlin.model.db.Schedule
import main.kotlin.model.db.Schedules
import main.kotlin.model.db.User
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.ScheduleResponse
import main.kotlin.net.Registry
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.UUID

class ScheduleService : BaseService() {
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

    fun findByUUID(uuid: String) = transaction {
        Employee.find {
            Employees.uuid eq UUID.fromString(uuid)
        }.firstOrNull()
    }

    fun getSchedules(user: User, employeeUUID: String, limit: Int, offset: Long): List<ScheduleResponse> {
        val employee = Registry.employeeService.findByUUID(user, employeeUUID) ?: return listOf()
        if (!isAuthorizedForEmployee(user, employee)) return listOf()

        return transaction {
            Schedule.find {
                Schedules.employee eq employee.id
            }.limit(limit, offset = offset)
        }.map(Schedule::toDataClass)
    }

    fun getAllSchedules(user: User, limit: Int, offset: Long): List<ScheduleResponse> {
        return transaction {
            val employees = Employee.find { Employees.employer eq user.id }.map { it.id }

            Schedule.find {
                Schedules.employee inList employees
            }.limit(limit, offset).map(Schedule::toDataClass)
        }
    }

    fun deleteSchedule(user: User, scheduleUUID: String): Boolean {
        val schedule = Schedule.find { Schedules.uuid eq UUID.fromString(scheduleUUID) }.firstOrNull() ?: return false
        if (!isAuthorizedForEmployee(user, schedule.employee)) return false

        return transaction {
            schedule.delete()
            true
        }
    }
}