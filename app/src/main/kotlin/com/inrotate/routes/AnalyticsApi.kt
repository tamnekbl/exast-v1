package com.inrotate.routes

import com.inrotate.analytics.*
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.dto.EventDraftRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val analyticsLogger = LoggerFactory.getLogger("AnalyticsApi")

fun Route.configureAnalytics(
    analyticsService: AiAnalyticsService,
    datasetBuilder: AiTrainingDatasetBuilder,
) {
    route("/analytics") {
        get("/health") {
            call.respond(HttpStatusCode.OK, analyticsService.checkAiHealth())
        }

        get("/dataset") {
            val csvBytes = datasetBuilder.buildCsv()
            call.respondBytes(
                bytes = csvBytes,
                contentType = ContentType.Text.CSV.withParameter("charset", "utf-8"),
                status = HttpStatusCode.OK,
            )
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
            val request = try {
                call.receive<EventDraftRequest>()
            } catch (e: Exception) {
                analyticsLogger.warn("Invalid analytics draft request", e)
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Некорректные параметры мероприятия", false),
                )
            }

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
        analyticsLogger.warn("Analytics request failed: ${e.message}", e)
        call.respond(e.toHttpStatus(), ApiResponse(e.message, false))
    }
}

private fun AnalyticsException.toHttpStatus(): HttpStatusCode = when (this) {
    is AiServiceDisabledException -> HttpStatusCode.ServiceUnavailable
    is AiServiceUnavailableException -> HttpStatusCode.ServiceUnavailable
    is AiServiceTimeoutException -> HttpStatusCode.GatewayTimeout
    is AiServiceBadResponseException -> HttpStatusCode.BadGateway
    is AiTrainingFailedException -> HttpStatusCode.BadRequest
    is AiPredictionFailedException -> HttpStatusCode.Conflict
    is AnalyticsValidationException -> HttpStatusCode.BadRequest
    is AnalyticsEntityNotFoundException -> HttpStatusCode.NotFound
    else -> HttpStatusCode.InternalServerError
}
