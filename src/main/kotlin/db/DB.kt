package main.kotlin.db

import main.kotlin.utils.Config
import org.jetbrains.exposed.sql.Database

object DB {
    val connect by lazy {
        val conf = Config.database
        Database.connect(
            "jdbc:pgsql://${conf.host}:${conf.port}/${conf.dbname}",
            driver = "com.impossibl.postgres.jdbc.PGDriver",
            user = conf.username,
            password = conf.password
        )
    }
}