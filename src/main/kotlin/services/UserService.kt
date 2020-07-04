package main.kotlin.services

import main.kotlin.model.db.User
import main.kotlin.model.db.Users
import org.jetbrains.exposed.sql.transactions.transaction

interface UserServiceInterface {
    fun exists(email: String): Boolean
    fun create(email: String, password: String, firstname: String?, lastname: String?): User?
    fun findByEmail(email: String): User?
}

class UserService: UserServiceInterface {
    override fun exists(email: String): Boolean {
        return transaction {
            !User.find { Users.email eq email }.empty()
        }
    }

    override fun create(
        email: String,
        password: String,
        firstname: String?,
        lastname: String?
    ): User? {
        if (exists(email)) return null

        return transaction {
            User.new {
                this.email = email
                this.password = password
                this.firstname = firstname
                this.lastname = lastname
            }
        }
    }

    override fun findByEmail(email: String): User? {
        return transaction {
            User.find { Users.email eq email }.firstOrNull()
        }
    }
}