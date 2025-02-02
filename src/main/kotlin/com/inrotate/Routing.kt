package com.inrotate

import com.inrotate.api.configureEvents
import com.inrotate.api.configureStructures
import com.inrotate.api.configureSubstructures
import com.inrotate.api.configureUsers
import com.inrotate.repository.EventQueries
import com.inrotate.repository.StructureQueries
import com.inrotate.repository.SubstructureQueries
import com.inrotate.repository.UserQueries
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    val eventRepository = EventQueries()
    val structureRepository = StructureQueries()
    val subtructureRepository = SubstructureQueries()
    val userRepository = UserQueries()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        route("/api") {
            configureEvents(eventRepository)
            configureStructures(structureRepository)
            configureSubstructures(subtructureRepository)
            configureUsers(userRepository)
        }
    }
}
