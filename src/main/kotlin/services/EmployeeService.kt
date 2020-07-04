package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.Employees
import main.kotlin.model.db.User
import main.kotlin.model.db.Users
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.EmployeeRequest
import main.kotlin.model.dto.EmployeeResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.math.BigDecimal
import java.util.UUID

class EmployeeService {
    fun create(
        creator: User,
        entryDate: DateTime?,
        weeklyHours: BigDecimal?,
        firstname: String,
        lastname: String
    ) = transaction {
        Employee.new {
            this.creator = creator
            this.employer = creator
            this.weeklyHours = weeklyHours
            this.entryDate = entryDate
            this.firstname = firstname
            this.lastname = lastname
        }
    }.toDataClass()

    fun findByUUID(uuid: String) = transaction {
        Employee.find { Employees.uuid eq UUID.fromString(uuid) }.firstOrNull()
    }
}