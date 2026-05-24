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
    val datasetSize: Int,
    val metrics: Map<String, Double>,
    val status: String,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class AttendancePredictionDto(
    val predictedParticipants: Double,
    val modelVersion: String,
    val modelTrainedAt: String?,
    val metrics: Map<String, Double>?,
    val warnings: List<String> = emptyList(),
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
    val startedAt: String,
    val endedAt: String?,
    val level: EventLevel,
    val location: String?,
    val format: EventFormat,
    val organizationRole: OrganizationRole,
    val types: List<EventType> = emptyList(),
    val organizations: List<Int> = emptyList(),
)
