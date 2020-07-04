package main.kotlin.controllers

import main.kotlin.model.dto.AuthResponse
import main.kotlin.model.dto.Login
import main.kotlin.model.dto.SignUp
import main.kotlin.model.dto.TokenCheck
import main.kotlin.net.Registry
import main.kotlin.net.Responses
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

object AuthController {
    private val authService = Registry.authService

    val signUpAction = Body.auto<SignUp>().toLens()
    val loginAction = Body.auto<Login>().toLens()
    val isValidTokenAction = Body.auto<TokenCheck>().toLens()

    private val authResponse = Body.auto<AuthResponse>().toLens()

    fun routes(): RoutingHttpHandler {
        val signUp: HttpHandler = { request ->
            authResponse(
                authService.signUp(signUpAction(request)),
                Responses.OK
            )
        }

        val login: HttpHandler = { request ->
            authResponse(
                authService.login(loginAction(request)),
                Responses.OK
            )
        }

        val isValidToken: HttpHandler = { request ->
            val response = AuthResponse(
                isValidTokenAction(request).token,
                authService.authorize(isValidTokenAction(request).token) !== null
            )

            authResponse(
                response,
                Responses.OK
            )
        }

        return org.http4k.routing.routes(
            "/signup" bind Method.POST to signUp,
            "/login" bind Method.POST to login,
            "/token/valid" bind Method.POST to isValidToken
        )
    }
}