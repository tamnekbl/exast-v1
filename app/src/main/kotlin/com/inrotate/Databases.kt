package com.inrotate

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database


fun Application.configureDatabases() {
    val dotenv = Dotenv.load()

    val dbUrl = dotenv["DB_URL"] ?: throw IllegalStateException("DB_URL not set")
    val dbUser = dotenv["DB_USER"] ?: throw IllegalStateException("DB_USER not set")
    val dbPassword = dotenv["DB_PASSWORD"] ?: throw IllegalStateException("DB_PASSWORD not set")

    Database.connect(
        dbUrl,
        user = dbUser,
        password = dbPassword
    )
}
