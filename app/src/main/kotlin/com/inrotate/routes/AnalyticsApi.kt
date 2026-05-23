package com.inrotate.routes

import com.inrotate.analytics.AiServiceConfig
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureAnalytics(aiServiceConfig: AiServiceConfig) {
    route("/analytics") {
        post("/models/train") {
            if (!aiServiceConfig.enabled) {
                return@post call.respondAiDisabled()
            }

            call.respond(
                HttpStatusCode.NotImplemented,
                ApiResponse("AI analytics integration is not implemented yet", false)
            )
        }

        post("/predict") {
            if (!aiServiceConfig.enabled) {
                return@post call.respondAiDisabled()
            }

            call.respond(
                HttpStatusCode.NotImplemented,
                ApiResponse("AI analytics integration is not implemented yet", false)
            )
        }

        get("/models/latest") {
            if (!aiServiceConfig.enabled) {
                return@get call.respondAiDisabled()
            }

            call.respond(
                HttpStatusCode.NotImplemented,
                ApiResponse("AI analytics integration is not implemented yet", false)
            )
        }

        get("/models") {
            if (!aiServiceConfig.enabled) {
                return@get call.respondAiDisabled()
            }

            call.respond(
                HttpStatusCode.NotImplemented,
                ApiResponse("AI analytics integration is not implemented yet", false)
            )
        }
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.respondAiDisabled() {
    respond(
        HttpStatusCode.ServiceUnavailable,
        ApiResponse("AI service integration is disabled", false)
    )
}
