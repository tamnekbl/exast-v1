package com.inrotate.api

import com.inrotate.db.substructures.Substructure
import com.inrotate.repository.SubstructureRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSubstructures(structureRepository: SubstructureRepository) {
    routing {
        route("/structures") {
            get {
                val name = call.request.queryParameters["name"]

                val structures = structureRepository.getFiltered(name)
                if (structures.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, structures)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }  //todo валидация id
                val substructure = structureRepository.getById(id)
                if (substructure == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(substructure)
            }

            post {
                try {
                    val substructure = call.receive<Substructure>()
                    structureRepository.add(substructure)
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Substructure added successfully", true)
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(e.message, false)
                    )
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse("Invalid or missing ID", false)
                    )
                    return@put
                }

                structureRepository.getById(id)
                    ?: return@put call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse("No substructure with id $id", false)
                    )

                try {
                    val updatedSubstructure = call.receive<Substructure>() // Получаем объект из тела запроса

                    structureRepository.edit(id, updatedSubstructure) // Вызываем функцию обновления
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Substructure updated successfully", true)
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse(e.message, false)
                    )
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse("Invalid or missing ID", false)
                    )
                    return@delete
                }

                structureRepository.getById(id)
                    ?: return@delete call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse("No substructure with id $id", false)
                    )

                try {
                    structureRepository.remove(id)
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Substructure deleted correctly", true)
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
}