package com.inrotate.routes

import com.inrotate.analytics.AiAnalyticsService
import com.inrotate.analytics.AnalyticsException
import com.inrotate.analytics.dto.EventDraftRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureAnalytics(analyticsService: AiAnalyticsService) {
    route("/analytics") {
        get("/health") {
            call.respond(HttpStatusCode.OK, analyticsService.checkAiHealth())
        }

        post("/train") {
            respondAnalytics {
                analyticsService.trainAttendanceModel()
            }
        }

        post("/events/{eventId}/predict-attendance") {
            val eventId = call.parameters["eventId"]?.toIntOrNull()
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Invalid or missing eventId", false),
                )

            respondAnalytics {
                analyticsService.predictAttendanceForEvent(eventId)
            }
        }

        post("/predict-attendance") {
            val request = call.receive<EventDraftRequest>()

            respondAnalytics {
                analyticsService.predictAttendanceForDraft(request)
            }
        }

        get("/models/latest") {
            respondAnalytics {
                analyticsService.getLatestModel()
            }
        }

        get("/models") {
            respondAnalytics {
                analyticsService.getModels()
            }
        }
    }
}

private suspend inline fun <reified T : Any> RoutingContext.respondAnalytics(
    crossinline block: suspend () -> T,
) {
    try {
        call.respond(HttpStatusCode.OK, block())
    } catch (e: AnalyticsException) {
        call.respond(e.toHttpStatus(), ApiResponse(e.message, false))
    }
}

private fun AnalyticsException.toHttpStatus(): HttpStatusCode = when (code) {
    AnalyticsException.Code.AI_DISABLED,
    AnalyticsException.Code.AI_UNAVAILABLE -> HttpStatusCode.ServiceUnavailable

    AnalyticsException.Code.EVENT_NOT_FOUND,
    AnalyticsException.Code.ORGANIZATION_NOT_FOUND -> HttpStatusCode.NotFound

    AnalyticsException.Code.INVALID_REQUEST -> HttpStatusCode.BadRequest
}
