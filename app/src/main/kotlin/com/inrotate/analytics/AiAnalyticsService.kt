package com.inrotate.analytics

import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.*
import com.inrotate.analytics.ai.mapper.AiEventMapper
import com.inrotate.analytics.dto.*
import com.inrotate.models.Organization
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
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
            logger.info("Starting AI event scale model training")
            val csvBytes = datasetBuilder.buildCsv()
            val trainingRows = csvBytes.trainingRowCount()
            logger.info("AI training dataset built: rows={}, bytes={}", trainingRows, csvBytes.size)

            if (trainingRows == 0) {
                logger.warn("AI event scale model training skipped: not enough training data")
                throw AiTrainingFailedException("Недостаточно данных для обучения модели")
            }

            val result = aiServiceClient.train(csvBytes).toTrainingResultDto()
            logger.info(
                "AI event scale model training completed: modelVersion={}",
                result.modelVersion,
            )
            result
        } catch (e: AiTrainingFailedException) {
            logger.warn("AI event scale model training failed: {}", e.message, e)
            throw e
        } catch (e: AiServiceUnavailableException) {
            logger.warn("AI event scale model training failed: AI service unavailable", e)
            throw e
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI event scale model training integration error", e)
            throw e
        }
    }

    suspend fun predictAttendanceForEvent(eventId: Int): EventScalePredictionDto {
        ensureAiEnabled()

        val event = eventRepository.getById(eventId)
            ?: throw AnalyticsEntityNotFoundException("Мероприятие не найдено")

        return try {
            logger.info("Requesting AI event scale prediction for eventId={}", eventId)
            val prediction = aiServiceClient
                .predict(AiEventMapper.fromEvent(event))
                .toEventScalePredictionDto()
            logger.info(
                "AI event scale prediction received for eventId={}: predictedScale={}, confidence={}, modelVersion={}",
                eventId,
                prediction.predictedScale,
                prediction.confidence,
                prediction.modelVersion,
            )
            prediction
        } catch (e: AiModelNotFoundException) {
            logger.warn("AI event scale prediction unavailable for eventId={}: model is not trained", eventId, e)
            throw e
        } catch (e: AiServiceUnavailableException) {
            logger.warn("AI event scale prediction failed for eventId={}: AI service unavailable", eventId, e)
            throw e
        } catch (e: IllegalArgumentException) {
            logger.warn("AI event scale prediction failed for eventId={}: invalid event data", eventId, e)
            throw AnalyticsValidationException("Некорректные параметры мероприятия", e)
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI event scale prediction integration error for eventId={}", eventId, e)
            throw e
        }
    }

    suspend fun predictAttendanceForDraft(request: EventDraftRequest): EventScalePredictionDto {
        ensureAiEnabled()

        val organizations = loadOrganizations(request.organizations)

        return try {
            logger.info(
                "Requesting AI event scale prediction for draft event: organizationCount={}, typeCount={}",
                organizations.size,
                request.types.size,
            )
            val prediction = aiServiceClient
                .predict(AiEventMapper.fromDraftRequest(request, organizations))
                .toEventScalePredictionDto()
            logger.info(
                "AI event scale prediction received for draft event: predictedScale={}, confidence={}, modelVersion={}",
                prediction.predictedScale,
                prediction.confidence,
                prediction.modelVersion,
            )
            prediction
        } catch (e: AiModelNotFoundException) {
            logger.warn("AI event scale prediction unavailable for draft event: model is not trained", e)
            throw e
        } catch (e: AiServiceUnavailableException) {
            logger.warn("AI event scale prediction failed for draft event: AI service unavailable", e)
            throw e
        } catch (e: IllegalArgumentException) {
            logger.warn("AI event scale prediction failed for draft event: invalid request", e)
            throw AnalyticsValidationException("Некорректные параметры мероприятия", e)
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI event scale prediction integration error for draft event", e)
            throw e
        }
    }

    suspend fun getLatestModel(): ModelInfoDto {
        ensureAiEnabled()

        return try {
            val latestModel = aiServiceClient.getLatestModel().toModelInfoDto()
            logger.info("Latest AI model metadata received: modelVersion={}", latestModel.modelVersion)
            latestModel
        } catch (e: AiModelNotFoundException) {
            logger.warn("Latest AI model metadata unavailable: model is not trained", e)
            throw e
        } catch (e: AiServiceUnavailableException) {
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
        } catch (e: AiModelNotFoundException) {
            logger.warn("AI model list unavailable: model is not trained", e)
            throw e
        } catch (e: AiServiceUnavailableException) {
            logger.warn("AI model list request failed: AI service unavailable", e)
            throw e
        }
    }

    suspend fun getFeatureInsights(
        modelVersion: String? = null,
        topN: Int = 20,
    ): AiFeatureInsightsResponse {
        ensureAiEnabled()

        return try {
            logger.info(
                "Requesting AI feature insights: modelVersion={}, topN={}",
                modelVersion,
                topN,
            )
            val insights = aiServiceClient.getFeatureInsights(modelVersion, topN)
            logger.info(
                "AI feature insights received: modelVersion={}, eventsCount={}, topFeaturesCount={}",
                insights.model.modelVersion,
                insights.dataset.eventsCount,
                insights.featureImportance.topTransformedFeatures.size,
            )
            insights
        } catch (e: AiModelNotFoundException) {
            logger.warn("AI feature insights unavailable: model is not trained", e)
            throw e
        } catch (e: AiServiceUnavailableException) {
            logger.warn("AI feature insights request failed: AI service unavailable", e)
            throw e
        } catch (e: AnalyticsException) {
            logger.error("Unexpected AI feature insights integration error", e)
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

        val health = runCatching { aiServiceClient.health() }.getOrNull()
        val available = health?.status == "ok"
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
        metrics = metrics.numericMetrics(),
        baselineMetrics = baselineMetrics.numericMetrics(),
        warnings = warnings,
        featureSchema = featureSchema,
        classLabels = classLabels,
        classDescriptions = classDescriptions,
    )

    private fun AiEventScalePredictionResponse.toEventScalePredictionDto(): EventScalePredictionDto =
        EventScalePredictionDto(
            predictedScale = predictedScale,
            scaleDescription = description,
            participantsRange = participantsRange,
            probabilities = probabilities,
            confidence = confidence,
            similarEvents = similarEvents.map { it.toDto() },
            modelVersion = modelVersion,
            modelTrainedAt = modelTrainedAt,
            metrics = metrics.numericMetrics(),
            warnings = warnings,
        )

    private fun SimilarEventResponse.toDto(): SimilarEventDto = SimilarEventDto(
        title = title,
        description = description,
        dateStart = dateStart,
        dateEnd = dateEnd,
        level = level,
        format = format,
        organizationRole = organizationRole,
        mainType = mainType,
        mainOrganizationType = mainOrganizationType,
        participantsTotal = participantsTotal,
        eventScale = eventScale,
        similarity = similarity,
    )

    private fun AiModelMetadata.toModelInfoDto(): ModelInfoDto = ModelInfoDto(
        modelVersion = modelVersion,
        trainedAt = trainedAt,
        metrics = metrics.numericMetrics(),
        baselineMetrics = baselineMetrics.numericMetrics(),
        warnings = warnings,
    )

    private fun ByteArray.trainingRowCount(): Int =
        toString(StandardCharsets.UTF_8)
            .lineSequence()
            .filter { it.isNotBlank() }
            .drop(1)
            .count()

    private companion object {
        val logger = LoggerFactory.getLogger(AiAnalyticsService::class.java)
    }
}

private fun Map<String, JsonElement>?.numericMetrics(): Map<String, Double> =
    orEmpty().mapNotNull { (key, value) ->
        val number = (value as? JsonPrimitive)?.doubleOrNull
        number?.let { key to it }
    }.toMap()
