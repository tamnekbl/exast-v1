package com.inrotate.routes

import com.inrotate.models.OrganizationTypeRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.OrganizationTypeRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureOrganizationTypes(
    organizationTypeRepository: OrganizationTypeRepository
) {
    route("/organizations/types") {
        get {
            val types = organizationTypeRepository.getAll()
            call.respond(HttpStatusCode.OK, types.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val type = organizationTypeRepository.getById(id)?.toResponse()
            if (type == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(type)
        }

        post {
            try {
                val updatedOrganizationTypeRequest = call.receive<OrganizationTypeRequest>()
                val organizationType = updatedOrganizationTypeRequest.toOrganizationType()
                organizationTypeRepository.add(organizationType)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization type added successfully", true)
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

            if (organizationTypeRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No organization type with id $id", false)
                )
                return@put
            }

            try {
                val updatedOrganizationTypeRequest = call.receive<OrganizationTypeRequest>()
                val updatedOrganizationType = updatedOrganizationTypeRequest.toOrganizationType(id)
                organizationTypeRepository.update(updatedOrganizationType)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization type updated successfully", true)
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

            organizationTypeRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No organization type with id $id", false)
                )

            try {
                organizationTypeRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Organization type deleted correctly", true)
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