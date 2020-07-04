package main.kotlin.ext

import main.kotlin.model.db.User
import main.kotlin.model.db.Users
import main.kotlin.net.Responses
import org.http4k.core.Request
import org.http4k.core.Response
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

val Request.userId: Int?
    get() = this.header("x-user-id")?.toInt()

fun Request.withUser(func: (User) -> Response): Response {
    if(this.userId == null) return Responses.BAD_REQUEST

    return transaction {
        User.find {
            Users.id eq this@withUser.userId
        }.firstOrNull()?.let { func(it) } ?: Responses.BAD_REQUEST
    }
}