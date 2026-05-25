package com.inrotate.analytics.dto

import com.inrotate.models.EventFormat
import com.inrotate.models.EventLevel
import com.inrotate.models.EventType
import com.inrotate.models.OrganizationRole
import kotlinx.serialization.Serializable

@Serializable
data class TrainingResultDto(
    val modelVersion: String,
    val trainedAt: String,
    val metrics: Map<String, Double>,
    val baselineMetrics: Map<String, Double> = emptyMap(),
    val warnings: List<String> = emptyList(),
    val featureSchema: List<String> = emptyList(),
    val classLabels: List<String> = emptyList(),
    val classDescriptions: Map<String, String> = emptyMap(),
)

@Serializable
data class EventScalePredictionDto(
    val predictedScale: String,
    val scaleDescription: String,
    val participantsRange: String,
    val probabilities: Map<String, Double>,
    val confidence: Double,
    val similarEvents: List<SimilarEventDto> = emptyList(),
    val modelVersion: String,
    val modelTrainedAt: String?,
    val metrics: Map<String, Double> = emptyMap(),
    val warnings: List<String> = emptyList(),
)

@Serializable
data class SimilarEventDto(
    val title: String? = null,
    val description: String? = null,
    val dateStart: String? = null,
    val dateEnd: String? = null,
    val level: String? = null,
    val format: String? = null,
    val organizationRole: String? = null,
    val mainType: String? = null,
    val mainOrganizationType: String? = null,
    val participantsTotal: Int? = null,
    val eventScale: String,
    val similarity: Double,
)

@Serializable
data class ModelInfoDto(
    val modelVersion: String,
    val trainedAt: String?,
    val metrics: Map<String, Double>?,
    val baselineMetrics: Map<String, Double>?,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class AiHealthDto(
    val enabled: Boolean,
    val available: Boolean,
    val message: String,
)

@Serializable
data class EventDraftRequest(
    val title: String,
    val description: String?,
    val dateStart: String,
    val dateEnd: String?,
    val timeStart: String?,
    val timeEnd: String?,
    val level: EventLevel,
    val location: String?,
    val format: EventFormat,
    val organizationRole: OrganizationRole,
    val types: List<EventType> = emptyList(),
    val organizations: List<Int> = emptyList(),
)
