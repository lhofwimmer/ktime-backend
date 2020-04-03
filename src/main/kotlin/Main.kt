package main.kotlin

import main.kotlin.utils.Logger
import org.jetbrains.exposed.sql.Database

fun main() {
    Logger.info("Start server")

    Database.connect(
        "jdbc:pgsql://localhost:5432/ktime",
        driver = "com.impossibl.postgres.jdbc.PGDriver",
        user = "postgres",
        password = "2001ag10"
    )
}