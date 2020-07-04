package main.kotlin.net

import main.kotlin.services.AuthService
import main.kotlin.services.EmployeeService
import main.kotlin.services.ScheduleService
import main.kotlin.services.TokenAuthService
import main.kotlin.services.UserService
import main.kotlin.utils.Config
import main.kotlin.utils.JWT
import main.kotlin.utils.JavaJWT

object Registry {
    private val jwt: JWT = JavaJWT(Config.server.jwtSecret, "example")

    val userService = UserService()
    val employeeService = EmployeeService()
    val scheduleService = ScheduleService()
    val authService: AuthService = TokenAuthService(userService, jwt)
}