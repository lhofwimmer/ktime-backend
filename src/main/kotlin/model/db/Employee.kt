package main.kotlin.model.db

import main.kotlin.model.dto.EmployeeResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.date

object Employees : IntIdTable() {
    val uuid = uuid("uuid").autoGenerate()
    val creator = reference("creator_id", Users)
    val weeklyHours = decimal("weeklyHours", 6, 2).nullable()
    val entryDate = date("entryDate").nullable()
    val firstname = varchar("firstname", 50)
    val lastname = varchar("lastname", 50)
    val employer = reference("employer_id", Users)
}

class Employee(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Employee>(Employees)

    var uuid by Employees.uuid
    var creator by User referencedOn Employees.creator
    var weeklyHours by Employees.weeklyHours
    var entryDate by Employees.entryDate
    var firstname by Employees.firstname
    var lastname by Employees.lastname
    var employer by User referencedOn Employees.employer
    val schedules by Schedule referrersOn Schedules.employee
}

fun Employee.toDataClass() = EmployeeResponse(
    uuid = this.uuid.toString(),
    firstname = this.firstname,
    lastname = this.lastname,
    employer = this.employer.uuid.toString(),
    entryDate = this.entryDate?.millis,
    weeklyHours = this.weeklyHours
)
