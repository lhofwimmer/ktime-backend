package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.Schedule
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.ScheduleResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope
import org.joda.time.DateTime

class ScheduleService {
    fun create(
        employee: Employee,
        scheduleStart: DateTime,
        scheduleEnd: DateTime
    ): ScheduleResponse {
        val schedule = transaction{
            Schedule.new {
                this.employee = employee
                this.scheduleStart = scheduleStart
                this.scheduleEnd = scheduleEnd
            }
        }

        return schedule.toDataClass()
    }


}