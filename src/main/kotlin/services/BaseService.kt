package main.kotlin.services

import main.kotlin.model.db.Employee
import main.kotlin.model.db.User
import org.jetbrains.exposed.sql.transactions.transaction

open class BaseService () {
    fun isAuthorizedForEmployee(user: User, employee: Employee) : Boolean = transaction {
        user.employees.contains(employee)
    }
}