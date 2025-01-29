package com.inrotate.api

import com.inrotate.db.structures.Structure
import com.inrotate.repository.StructureRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureStructures(structureRepository: StructureRepository) {
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
                val structure = structureRepository.getById(id)
                if (structure == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(structure)
            }

            post {
                try {
                    val structure = call.receive<Structure>()
                    structureRepository.add(structure)
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Structure added successfully", true)
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
                        ApiResponse("No structure with id $id", false)
                    )

                try {
                    val updatedStructure = call.receive<Structure>() // Получаем объект из тела запроса

                    structureRepository.edit(id, updatedStructure) // Вызываем функцию обновления
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Structure updated successfully", true)
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
                        ApiResponse("No structure with id $id", false)
                    )

                try {
                    structureRepository.remove(id)
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse("Structure deleted correctly", true)
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