package main.kotlin.utils

import main.kotlin.model.config.DatabaseConfig
import java.io.File
import java.nio.file.Paths

object Config {

    fun databaseConfig(): DatabaseConfig {
        lines("db.conf").
    }

    private val configDir = Paths.get("./config").toAbsolutePath().toString()

    private fun file(file: String) = File("$configDir/$file")

    private fun lines(file: String) = file(file).readLines()
}