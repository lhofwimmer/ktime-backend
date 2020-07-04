package main.kotlin.model.db

import main.kotlin.model.dto.ScheduleResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Schedules : IntIdTable() {
    val uuid = uuid("uuid").autoGenerate()
    val employee = reference("employee_id", Employees)
    val scheduleStart = datetime("scheduleStart")
    val scheduleEnd = datetime("scheduleEnd")
}

class Schedule(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Schedule>(Schedules)

    var uuid by Schedules.uuid
    var employee by Employee referencedOn Schedules.employee
    var scheduleStart by Schedules.scheduleStart
    var scheduleEnd by Schedules.scheduleEnd
}

fun Schedule.toDataClass() = ScheduleResponse(
    uuid = this.uuid.toString(),
    employee = this.employee.toDataClass(),
    scheduleStart = scheduleStart.millis,
    scheduleEnd = this.scheduleEnd.millis
)