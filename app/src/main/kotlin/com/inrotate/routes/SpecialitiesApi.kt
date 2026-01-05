package com.inrotate.routes

import com.inrotate.models.SpecialityRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.SpecialityRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureSpecialities(
    specialityRepository: SpecialityRepository
) {
    route("/specialities") {
        get {
            val specialities = specialityRepository.getAll()
            call.respond(HttpStatusCode.OK, specialities.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val speciality = specialityRepository.getById(id)?.toResponse()
            if (speciality == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(speciality)
        }

        post {
            try {
                val specialityRequest = call.receive<SpecialityRequest>()
                val speciality = specialityRequest.toSpeciality()
                specialityRepository.add(speciality)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Speciality added successfully", true)
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

            if (specialityRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No speciality with id $id", false)
                )
                return@put
            }

            try {
                val updatedSpecialityRequest = call.receive<SpecialityRequest>()
                val updatedSpeciality = updatedSpecialityRequest.toSpeciality(id)
                specialityRepository.update(updatedSpeciality)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Speciality updated successfully", true)
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

            specialityRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No speciality with id $id", false)
                )

            try {
                specialityRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Speciality deleted correctly", true)
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