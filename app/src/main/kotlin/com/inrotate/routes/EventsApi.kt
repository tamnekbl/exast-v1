package com.inrotate.routes

import com.inrotate.models.EventRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.EventRepository
import com.inrotate.services.EventService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureEvents(
    eventsRepository: EventRepository,
    eventService: EventService,
) {
    route("/db/events") {
        get {
            val name = call.request.queryParameters["name"]
            val startDate = call.request.queryParameters["start"]
            val endDate = call.request.queryParameters["end"]

            val events = eventsRepository.getFiltered(name, startDate, endDate)
            if (events.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, events.map { it.toResponse() })
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val event = eventsRepository.getById(id)?.toResponse()
            if (event == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(event)
        }

        post {
            try {
                val eventRequest = call.receive<EventRequest>()
                val event = eventService.createEventWithOrganizations(eventRequest)
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

            if (eventsRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No event with id $id", false)
                )
                return@put
            }

            try {
                val updatedEventRequest = call.receive<EventRequest>()
                val updatedEvent = updatedEventRequest.toEvent(id)
                eventsRepository.update(updatedEvent)
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

            eventsRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No event with id $id", false)
                )

            try {
                eventsRepository.delete(id)
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