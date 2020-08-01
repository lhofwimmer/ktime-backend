package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.Employees
import main.kotlin.model.db.User
import main.kotlin.model.db.toDataClass
import main.kotlin.model.dto.EmployeeResponse
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
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

    fun findByUUID(user: User, uuid: String) = transaction {
        Employee.find {
            (Employees.uuid eq UUID.fromString(uuid)) and
                (Employees.employer eq user.id)
        }.firstOrNull()
    }

    fun getEmployees(user: User, limit: Int, offset: Long): List<EmployeeResponse> {
        return transaction {
            Employees.select {
                Employees.employer eq user.id
            }.limit(limit, offset)
        }.map { row ->
            val employer = User.findById(row[Employees.employer]) ?: return listOf()

            EmployeeResponse(
                row[Employees.uuid].toString(),
                row[Employees.weeklyHours],
                row[Employees.entryDate]?.millis,
                row[Employees.firstname],
                row[Employees.lastname],
                employer.uuid.toString()
            )
        }
    }
}