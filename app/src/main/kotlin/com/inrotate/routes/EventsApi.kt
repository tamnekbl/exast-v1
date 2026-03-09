package com.inrotate.routes

import com.inrotate.models.EventRequest
import com.inrotate.models.importer.EventRaw
import com.inrotate.models.importer.XlsxParser
import com.inrotate.models.toResponse
import com.inrotate.repository.EventRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*

fun Route.configureEvents(
    eventRepository: EventRepository
) {
    route("/events") {
        get {
            val name = call.request.queryParameters["name"]
            val startDate = call.request.queryParameters["start"]
            val endDate = call.request.queryParameters["end"]

            val events = eventRepository.getFiltered(name, startDate, endDate)
            call.respond(HttpStatusCode.OK, events.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val event = eventRepository.getById(id)?.toResponse()
            if (event == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(event)
        }

        post {
            try {
                val eventRequest = call.receive<EventRequest>()
                val event = eventRequest.toEvent()
                eventRepository.add(event)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Event added successfully", true)
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

            if (eventRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No event with id $id", false)
                )
                return@put
            }

            try {
                val updatedEventRequest = call.receive<EventRequest>()
                val updatedEvent = updatedEventRequest.toEvent(id)
                eventRepository.update(updatedEvent)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Event updated successfully", true)
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

            eventRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No event with id $id", false)
                )

            try {
                eventRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Event deleted correctly", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(e.message, false)
                )
            }
        }

        post("/import/excel") {
            try {
                //todo ограничение на размер файла
                val multipart = call.receiveMultipart()
                val events = mutableListOf<EventRaw>()

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        // Используем runCatching или обычный try-catch для парсинга конкретного файла
                        runCatching { part.provider().toInputStream().use { XlsxParser.parseEvents(it) } }
                            .onSuccess { events.addAll(it) }
                            .onFailure {
                                throw IllegalArgumentException("Parsing error in file ${part.originalFileName}: ${it.message}")
                            }
                    }
                    part.dispose()
                }

                if (events.isEmpty()) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse("Файлы не найдены или пусты", false)
                    )
                }

                val addedEvents = eventRepository.addAll(events.map { it.toEvent() })

                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Imported successfully: ${addedEvents.size}", true)
                )

            } catch (e: Exception) {
                // Обработка общих ошибок: обрыв связи, слишком большой файл, ошибки парсинга
                call.respond(HttpStatusCode.BadRequest, ApiResponse(e.message ?: "Ошибка импорта", false))
            }
        }
    }
}