package main.kotlin

import main.kotlin.controllers.AuthController
import main.kotlin.controllers.UserController
import main.kotlin.db.DB
import main.kotlin.utils.Config
import main.kotlin.utils.Logger
import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.filter.CorsPolicy
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer

fun main() {
    Logger.info("Start server")

    DB.connect

    val app = ServerFilters.CatchLensFailure
        .then(ServerFilters.Cors(CorsPolicy(listOf("*"), listOf("Authorization", "x-user-id", "Content-Type"), Method.values().toList(), false)))
        .then(DebuggingFilters.PrintRequestAndResponse())
        .then(
            routes(
                "/api" bind routes(
                    "" bind UserController.routes(),
                    "/auth" bind AuthController.routes()
                )
            )
        )

    app.asServer(Netty(Config.server.port)).start()

    Logger.info("Server ready at port ${Config.server.port}")
}