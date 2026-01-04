package com.inrotate

import com.inrotate.repository.EventRepositoryImpl
import com.inrotate.repository.OrganizationRepositoryImpl
import com.inrotate.routes.configureEvents
import com.inrotate.routes.configureOrganizations
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

    val eventRepository = EventRepositoryImpl()
    val organizationRepository = OrganizationRepositoryImpl()
    /*
           val eventService = EventService(
                eventRepository,
                organizationRepository
            )*/

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        route("/api") {
            configureEvents(eventRepository)
            configureOrganizations(organizationRepository)
        }
    }
}