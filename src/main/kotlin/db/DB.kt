package main.kotlin.db

import org.jetbrains.exposed.sql.Database

object DB {
    val db by lazy {
        Database.connect(
            "jdbc:pgsql://localhost:5432/ktime",
            driver = "com.impossibl.postgres.jdbc.PGDriver",
            user = "postgres",
            password = "2001ag10"
        )
    }
}