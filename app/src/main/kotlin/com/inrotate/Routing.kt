package com.inrotate

import com.inrotate.repository.*
import com.inrotate.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
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
    val organizationTypeRepository = OrganizationTypeRepositoryImpl()
    val specialityRepository = SpecialityRepositoryImpl()
    val roleRepository = RoleRepositoryImpl()


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        authenticate("auth-basic") {
            route("/api/v1") {
                configureEvents(eventRepository)
                configureOrganizations(organizationRepository)
                configureOrganizationTypes(organizationTypeRepository)
                configureSpecialities(specialityRepository)
                configureRoles(roleRepository)
            }
        }

    }
}