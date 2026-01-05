package com.inrotate.api

import com.inrotate.models.Event
import com.inrotate.repository.EventRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureEvents(repository: EventRepository) {
    routing {
        route("/events") {
            get {
                val name = call.request.queryParameters["name"]
                val category = call.request.queryParameters["category"] //todo
                val startDate = call.request.queryParameters["start"]
                val endDate = call.request.queryParameters["end"]

                val events = repository.getFilteredEvents(name, startDate, endDate)
                if (events.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, events)
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
                val event = repository.eventById(id.toLong())
                if (event == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(event)
            }

            post {
                try {
                    val event = call.receive<Event>()
                    repository.addEvent(event)
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
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse("Invalid or missing ID", false)
                    )
                    return@put
                }

                repository.eventById(id)
                    ?: return@put call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse("No event with id $id", false)
                    )

                try {
                    val updatedEvent = call.receive<Event>() // Получаем объект из тела запроса

                    repository.editEvent(id, updatedEvent) // Вызываем функцию обновления
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
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse("Invalid or missing ID", false)
                    )
                    return@delete
                }

                repository.eventById(id)
                    ?: return@delete call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse("No event with id $id", false)
                    )

                try {
                    repository.removeEvent(id)
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
        }
    }
}