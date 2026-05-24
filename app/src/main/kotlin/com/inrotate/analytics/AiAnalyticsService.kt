package com.inrotate.analytics

import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.client.AiServiceException
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.AiModelMetadata
import com.inrotate.analytics.ai.dto.AiPredictionResponse
import com.inrotate.analytics.ai.dto.AiTrainingResponse
import com.inrotate.analytics.ai.mapper.AiEventMapper
import com.inrotate.analytics.dto.*
import com.inrotate.models.Organization
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository

class AiAnalyticsService(
    private val config: AiServiceConfig,
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
    private val datasetBuilder: AiTrainingDatasetBuilder,
    private val aiServiceClient: AiServiceClient,
) {
    suspend fun trainAttendanceModel(): TrainingResultDto {
        ensureAiEnabled()

        return callAi("train attendance model") {
            val csvBytes = datasetBuilder.buildCsv()
            aiServiceClient.train(csvBytes).toTrainingResultDto()
        }
    }

    suspend fun predictAttendanceForEvent(eventId: Int): AttendancePredictionDto {
        ensureAiEnabled()

        val event = eventRepository.getById(eventId)
            ?: throw AnalyticsException(
                message = "Event with id $eventId not found",
                code = AnalyticsException.Code.EVENT_NOT_FOUND,
            )

        return callAi("predict attendance for event $eventId") {
            aiServiceClient
                .predict(AiEventMapper.fromEvent(event))
                .toAttendancePredictionDto()
        }
    }

    suspend fun predictAttendanceForDraft(request: EventDraftRequest): AttendancePredictionDto {
        ensureAiEnabled()

        val organizations = loadOrganizations(request.organizations)

        return callAi("predict attendance for draft event") {
            aiServiceClient
                .predict(AiEventMapper.fromDraftRequest(request, organizations))
                .toAttendancePredictionDto()
        }
    }

    suspend fun getLatestModel(): ModelInfoDto {
        ensureAiEnabled()

        return callAi("get latest model") {
            aiServiceClient.getLatestModel().toModelInfoDto()
        }
    }

    suspend fun getModels(): List<ModelInfoDto> {
        ensureAiEnabled()

        return callAi("get models") {
            aiServiceClient.getModels().map { it.toModelInfoDto() }
        }
    }

    suspend fun checkAiHealth(): AiHealthDto {
        if (!config.enabled) {
            return AiHealthDto(
                enabled = false,
                available = false,
                message = "AI service integration is disabled",
            )
        }

        val available = aiServiceClient.health()
        return AiHealthDto(
            enabled = true,
            available = available,
            message = if (available) {
                "AI service is available"
            } else {
                "AI service is unavailable"
            },
        )
    }

    private fun ensureAiEnabled() {
        if (!config.enabled) {
            throw AnalyticsException(
                message = "AI service integration is disabled",
                code = AnalyticsException.Code.AI_DISABLED,
            )
        }
    }

    private suspend fun loadOrganizations(ids: List<Int>): List<Organization> {
        val organizations = ids.map { id ->
            organizationRepository.getById(id)
                ?: throw AnalyticsException(
                    message = "Organization with id $id not found",
                    code = AnalyticsException.Code.ORGANIZATION_NOT_FOUND,
                )
        }

        return organizations
    }

    private suspend fun <T> callAi(operation: String, block: suspend () -> T): T = try {
        block()
    } catch (e: AiServiceException) {
        throw AnalyticsException(
            message = "AI service is unavailable during $operation",
            code = AnalyticsException.Code.AI_UNAVAILABLE,
            cause = e,
        )
    } catch (e: IllegalArgumentException) {
        throw AnalyticsException(
            message = "Invalid analytics request during $operation: ${e.message}",
            code = AnalyticsException.Code.INVALID_REQUEST,
            cause = e,
        )
    }

    private fun AiTrainingResponse.toTrainingResultDto(): TrainingResultDto = TrainingResultDto(
        modelVersion = modelVersion,
        trainedAt = trainedAt,
        datasetSize = datasetSize,
        metrics = metrics,
        status = status,
        warnings = warnings,
    )

    private fun AiPredictionResponse.toAttendancePredictionDto(): AttendancePredictionDto = AttendancePredictionDto(
        predictedParticipants = predictedParticipants,
        modelVersion = modelVersion,
        modelTrainedAt = modelTrainedAt,
        metrics = metrics,
        warnings = warnings,
    )

    private fun AiModelMetadata.toModelInfoDto(): ModelInfoDto = ModelInfoDto(
        modelVersion = modelVersion,
        trainedAt = trainedAt,
        metrics = metrics,
        baselineMetrics = baselineMetrics,
        warnings = warnings.orEmpty(),
    )
}
