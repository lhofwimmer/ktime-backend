package main.kotlin.utils

import main.kotlin.model.config.DatabaseConfig
import main.kotlin.model.config.ServerConfig
import java.io.File
import java.nio.file.Paths

object Config {
    val database by lazy {
        lines("db.cfg").let {
            DatabaseConfig(
                it[0],
                it[1],
                it[2],
                it[3].toInt(),
                it[4]
            )
        }
    }

    val server by lazy {
        lines("server.cfg").let {
            ServerConfig(
                it[0].toInt(),
                it[1]
            )
        }
    }

    private val configDir = Paths.get("./config").toAbsolutePath().toString()

    private fun file(file: String) = File("$configDir/$file")

    private fun lines(file: String) = file(file).readLines()
}