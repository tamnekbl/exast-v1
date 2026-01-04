package com.inrotate.routes

import com.inrotate.models.OrganizationRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.OrganizationRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureOrganizations(
    organizationRepository: OrganizationRepository
) {
    route("/v1/organizations") {
        get {
            val organizations = organizationRepository.getAll()
            call.respond(HttpStatusCode.OK, organizations.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val organization = organizationRepository.getById(id)?.toResponse()
            if (organization == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(organization)
        }

        post {
            try {
                val organizationRequest = call.receive<OrganizationRequest>()
                val organization = organizationRequest.toOrganization()
                organizationRepository.add(organization)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization added successfully", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(e.message, false)
                )
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Invalid or missing ID", false)
                )
                return@put
            }

            if (organizationRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No organization with id $id", false)
                )
                return@put
            }

            try {
                val updatedOrganizationRequest = call.receive<OrganizationRequest>()
                val updatedOrganization = updatedOrganizationRequest.toOrganization(id)
                organizationRepository.update(updatedOrganization)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization updated successfully", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(e.message, false)
                )
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Invalid or missing ID", false)
                )
                return@delete
            }

            organizationRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No organization with id $id", false)
                )

            try {
                organizationRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization deleted correctly", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(e.message, false)
                )
            }
        }
    }
}