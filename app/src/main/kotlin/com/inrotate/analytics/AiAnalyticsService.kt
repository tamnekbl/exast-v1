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
import org.slf4j.LoggerFactory
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
            logger.info("Starting AI attendance model training")
            val csvBytes = datasetBuilder.buildCsv()
            val trainingRows = csvBytes.trainingRowCount()
            logger.info("AI training dataset built: rows={}, bytes={}", trainingRows, csvBytes.size)

            if (trainingRows == 0) {
                logger.warn("AI attendance model training skipped: not enough training data")
                throw AiTrainingFailedException("Недостаточно данных для обучения модели")
            }

            val result = aiServiceClient.train(csvBytes).toTrainingResultDto()
            logger.info(
                "AI attendance model training completed: modelVersion={}, datasetSize={}",
                result.modelVersion,
                result.datasetSize,
            )
            result
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                logger.warn("AI attendance model training rejected by AI service: statusCode={}", e.statusCode, e)
                throw AiTrainingFailedException("Недостаточно данных для обучения модели", e)
            }
            logger.warn("AI attendance model training failed: AI service unavailable", e)
            throw e
        } catch (e: AiTrainingFailedException) {
            logger.warn("AI attendance model training failed: {}", e.message, e)
            throw e
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI attendance model training integration error", e)
            throw e
        }
    }

    suspend fun predictAttendanceForEvent(eventId: Int): AttendancePredictionDto {
        ensureAiEnabled()

        val event = eventRepository.getById(eventId)
            ?: throw AnalyticsEntityNotFoundException("Мероприятие не найдено")

        return try {
            logger.info("Requesting AI attendance prediction for eventId={}", eventId)
            val prediction = aiServiceClient
                .predict(AiEventMapper.fromEvent(event))
                .toAttendancePredictionDto()
            logger.info(
                "AI attendance prediction received for eventId={}: predictedParticipants={}, modelVersion={}",
                eventId,
                prediction.predictedParticipants,
                prediction.modelVersion,
            )
            prediction
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                logger.warn("AI attendance prediction unavailable for eventId={}: model is not trained", eventId, e)
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            logger.warn("AI attendance prediction failed for eventId={}: AI service unavailable", eventId, e)
            throw e
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI attendance prediction integration error for eventId={}", eventId, e)
            throw e
        }
    }

    suspend fun predictAttendanceForDraft(request: EventDraftRequest): AttendancePredictionDto {
        ensureAiEnabled()

        val organizations = loadOrganizations(request.organizations)

        return try {
            logger.info(
                "Requesting AI attendance prediction for draft event: organizationCount={}, typeCount={}",
                organizations.size,
                request.types.size,
            )
            val prediction = aiServiceClient
                .predict(AiEventMapper.fromDraftRequest(request, organizations))
                .toAttendancePredictionDto()
            logger.info(
                "AI attendance prediction received for draft event: predictedParticipants={}, modelVersion={}",
                prediction.predictedParticipants,
                prediction.modelVersion,
            )
            prediction
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                logger.warn("AI attendance prediction unavailable for draft event: model is not trained", e)
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            logger.warn("AI attendance prediction failed for draft event: AI service unavailable", e)
            throw e
        } catch (e: IllegalArgumentException) {
            logger.warn("AI attendance prediction failed for draft event: invalid request", e)
            throw AnalyticsValidationException("Некорректные параметры мероприятия", e)
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI attendance prediction integration error for draft event", e)
            throw e
        }
    }

    suspend fun getLatestModel(): ModelInfoDto {
        ensureAiEnabled()

        return try {
            val latestModel = aiServiceClient.getLatestModel().toModelInfoDto()
            logger.info("Latest AI model metadata received: modelVersion={}", latestModel.modelVersion)
            latestModel
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                logger.warn("Latest AI model metadata unavailable: model is not trained", e)
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            logger.warn("Latest AI model metadata request failed: AI service unavailable", e)
            throw e
        }
    }

    suspend fun getModels(): List<ModelInfoDto> {
        ensureAiEnabled()

        return try {
            val models = aiServiceClient.getModels().map { it.toModelInfoDto() }
            logger.info("AI model list received: count={}", models.size)
            models
        } catch (e: AiServiceUnavailableException) {
            if (e.statusCode in CLIENT_ERROR_STATUS_CODES) {
                logger.warn("AI model list unavailable: model is not trained", e)
                throw AiPredictionFailedException("Модель прогнозирования еще не обучена", e)
            }
            logger.warn("AI model list request failed: AI service unavailable", e)
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
        if (available) {
            logger.info("AI service health check succeeded")
        } else {
            logger.warn("AI service health check failed")
        }
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

    private fun ByteArray.trainingRowCount(): Int =
        toString(StandardCharsets.UTF_8)
            .lineSequence()
            .filter { it.isNotBlank() }
            .drop(1)
            .count()

    private companion object {
        val logger = LoggerFactory.getLogger(AiAnalyticsService::class.java)
        val CLIENT_ERROR_STATUS_CODES = 400..499
    }
}
