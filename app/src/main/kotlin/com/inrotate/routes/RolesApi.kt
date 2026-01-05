package com.inrotate.routes

import com.inrotate.models.RoleRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.RoleRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureRoles(
    roleRepository: RoleRepository
) {
    route("/roles") {
        get {
            val roles = roleRepository.getAll()
            call.respond(HttpStatusCode.OK, roles.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val role = roleRepository.getById(id)?.toResponse()
            if (role == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(role)
        }

        post {
            try {
                val roleRequest = call.receive<RoleRequest>()
                val role = roleRequest.toRole()
                roleRepository.add(role)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Role added successfully", true)
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

            if (roleRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No role with id $id", false)
                )
                return@put
            }

            try {
                val updatedRoleRequest = call.receive<RoleRequest>()
                val updatedRole = updatedRoleRequest.toRole(id)
                roleRepository.update(updatedRole)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Role updated successfully", true)
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

            roleRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No role with id $id", false)
                )

            try {
                roleRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Role deleted correctly", true)
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