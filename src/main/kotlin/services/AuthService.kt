package main.kotlin.services

import com.lambdaworks.crypto.SCryptUtil
import main.kotlin.model.db.User
import main.kotlin.model.dto.AuthResponse
import main.kotlin.model.dto.Login
import main.kotlin.model.dto.SignUp
import main.kotlin.model.dto.UserInfo
import main.kotlin.utils.JWT
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.Duration

typealias AccessToken = String

interface AuthService {
    fun signUp(data: SignUp): AuthResponse
    fun login(data: Login): AuthResponse
    fun authorize(token: String): Int?
    fun me(id: Int): UserInfo?
}

class TokenAuthService(
    private val userService: UserService,
    private val jwt: JWT
) : AuthService {
    private val unsuccessfulAuth = AuthResponse(null,false)
    private val expiresIn = Duration.standardDays(30)

    override fun signUp(data: SignUp): AuthResponse {
        val (firstname, lastname, email, password) = data

        if(password.isBlank() || email.isBlank() || userService.exists(email)) return unsuccessfulAuth

        val hashedPassword = SCryptUtil.scrypt(password, 16384, 8, 1)
        val user = userService.create(email, hashedPassword, firstname, lastname) ?: return unsuccessfulAuth

        return AuthResponse(
            jwt.create(user.email, expiresIn),
            true
        )
    }

    override fun login(data: Login): AuthResponse {
        val user = userService.findByEmail(data.email) ?: return unsuccessfulAuth

        if (!SCryptUtil.check(data.password, user.password)) return unsuccessfulAuth

        return AuthResponse(
            jwt.create(user.email, expiresIn),
            true
        )
    }

    override fun authorize(token: String): Int? {
        val email = jwt.verify(token) ?: return null

        return userService.findByEmail(email)?.id?.value
    }

    override fun me(id: Int): UserInfo? {
        val user = transaction {
            User.findById(id)
        } ?: return null

        return UserInfo(
            user.uuid,
            user.firstname,
            user.lastname,
            user.email
        )
    }

}