package com.inrotate.routes

import com.inrotate.models.ParticipantRequest
import com.inrotate.models.toResponse
import com.inrotate.repository.ParticipantRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//todo пока не используется. основное внимание сейчас на сами события

fun Route.configureParticipants(
    participantRepository: ParticipantRepository
) {
    route("/participants") {
        get {
            val participants = participantRepository.getAll()
            call.respond(HttpStatusCode.OK, participants.map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val participant = participantRepository.getById(id)?.toResponse()
            if (participant == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(participant)
        }

        post {
            try {
                val participantRequest = call.receive<ParticipantRequest>()
                val participant = participantRequest.toParticipant()
                participantRepository.add(participant)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Participant added successfully", true)
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

            if (participantRepository.getById(id) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No participant with id $id", false)
                )
                return@put
            }

            try {
                val updatedParticipantRequest = call.receive<ParticipantRequest>()
                val updatedParticipant = updatedParticipantRequest.toParticipant(id)
                participantRepository.update(updatedParticipant)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Participant updated successfully", true)
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

            participantRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No participant with id $id", false)
                )

            try {
                participantRepository.delete(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("Participant deleted correctly", true)
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