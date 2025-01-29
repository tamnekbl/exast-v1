package com.inrotate

import com.inrotate.api.configureEvents
import com.inrotate.api.configureStructures
import com.inrotate.db.configureDatabases
import com.inrotate.repository.EventQueries
import com.inrotate.repository.StructureQueries
import com.inrotate.repository.SubstructureQueries
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val eventRepository = EventQueries()
    val structureRepository = StructureQueries()
    val subtructureRepository = SubstructureQueries()

    configureSerialization()
    configureEvents(eventRepository)
    configureDatabases()
    configureRouting()
    configureStructures(structureRepository)
    configureStructures(structureRepository)

}
