package com.inrotate.analytics

import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.AiModelMetadata
import com.inrotate.analytics.ai.dto.AiPredictionResponse
import com.inrotate.analytics.ai.dto.AiTrainingResponse
import com.inrotate.analytics.ai.mapper.AiEventMapper
import com.inrotate.analytics.dto.*
import com.inrotate.models.Organization
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository
import java.nio.charset.StandardCharsets

class AiAnalyticsService(
    private val config: AiServiceConfig,
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
    private val datasetBuilder: AiTrainingDatasetBuilder,
    private val aiServiceClient: AiServiceClient,
) {
    suspend fun trainAttendanceModel(): TrainingResultDto {
        ensureAiEnabled()

        return try {
            val csvBytes = datasetBuilder.buildCsv()
            if (!csvBytes.hasTrainingRows()) {
                throw AiTrainingFailedException("Недостаточно данных для обучения модели")
            }

            aiServiceClient.train(csvBytes).toTrainingResultDto()
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                throw AiTrainingFailedException("Недостаточно данных для обучения модели", e)
            }
            throw e
        }
    }

    suspend fun predictAttendanceForEvent(eventId: Int): AttendancePredictionDto {
        ensureAiEnabled()

        val event = eventRepository.getById(eventId)
            ?: throw AnalyticsEntityNotFoundException("Мероприятие не найдено")

        return try {
            aiServiceClient
                .predict(AiEventMapper.fromEvent(event))
                .toAttendancePredictionDto()
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            throw e
        }
    }

    suspend fun predictAttendanceForDraft(request: EventDraftRequest): AttendancePredictionDto {
        ensureAiEnabled()

        val organizations = loadOrganizations(request.organizations)

        return try {
            aiServiceClient
                .predict(AiEventMapper.fromDraftRequest(request, organizations))
                .toAttendancePredictionDto()
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            throw e
        } catch (e: IllegalArgumentException) {
            throw AnalyticsValidationException("Некорректные параметры мероприятия", e)
        }
    }

    suspend fun getLatestModel(): ModelInfoDto {
        ensureAiEnabled()

        return try {
            aiServiceClient.getLatestModel().toModelInfoDto()
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            throw e
        }
    }

    suspend fun getModels(): List<ModelInfoDto> {
        ensureAiEnabled()

        return try {
            aiServiceClient.getModels().map { it.toModelInfoDto() }
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            throw e
        }
    }

    suspend fun checkAiHealth(): AiHealthDto {
        if (!config.enabled) {
            return AiHealthDto(
                enabled = false,
                available = false,
                message = "Сервис интеллектуального анализа отключен",
            )
        }

        val available = aiServiceClient.health()
        return AiHealthDto(
            enabled = true,
            available = available,
            message = if (available) {
                "Сервис интеллектуального анализа доступен"
            } else {
                "Сервис интеллектуального анализа временно недоступен"
            },
        )
    }

    private fun ensureAiEnabled() {
        if (!config.enabled) {
            throw AiServiceDisabledException()
        }
    }

    private suspend fun loadOrganizations(ids: List<Int>): List<Organization> =
        ids.map { id ->
            organizationRepository.getById(id)
                ?: throw AnalyticsEntityNotFoundException("Организация не найдена")
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

    private fun ByteArray.hasTrainingRows(): Boolean =
        toString(StandardCharsets.UTF_8)
            .lineSequence()
            .filter { it.isNotBlank() }
            .count() > 1

    private companion object {
        val CLIENT_ERROR_STATUS_CODES = 400..499
    }
}
