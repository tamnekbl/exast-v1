package com.inrotate

import com.inrotate.api.configureEvents
import com.inrotate.db.configureDatabases
import com.inrotate.repository.EventQueries
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

    val dotenv = Dotenv.load()
    val dbUrl = dotenv["DB_URL"]
    val dbUser = dotenv["DB_USER"]
    val dbPassword = dotenv["DB_PASSWORD"]
}

fun Application.module() {
    val eventRepository = EventQueries()

    configureSerialization()
    configureEvents(eventRepository)
    configureDatabases()
    configureRouting()

}
