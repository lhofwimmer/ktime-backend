package main.kotlin.net

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response

object Filters {
    object AuthFilter: Filter {
        override fun invoke(next: HttpHandler): HttpHandler = {
            it.header("Authorization")?.let {token ->
                Registry.authService.authorize(token)?.let { id ->
                    next(it.header("x-user-id", id.toString()))
                }
            } ?: Responses.UNAUTHORIZED
        }
    }
}